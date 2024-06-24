package com.lvji.lvjiojbackendquestionservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvji.lvjiojbackendcommon.common.ErrorCode;
import com.lvji.lvjiojbackendcommon.constant.CommonConstant;
import com.lvji.lvjiojbackendcommon.exception.BusinessException;
import com.lvji.lvjiojbackendcommon.utils.SqlUtils;
import com.lvji.lvjiojbackendmodel.dto.questionsubmit.QuestionSubmitAddRequest;
import com.lvji.lvjiojbackendmodel.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.lvji.lvjiojbackendmodel.entity.Question;
import com.lvji.lvjiojbackendmodel.entity.QuestionSubmit;
import com.lvji.lvjiojbackendmodel.entity.User;
import com.lvji.lvjiojbackendmodel.enums.QuestionSubmitLanguageEnum;
import com.lvji.lvjiojbackendmodel.enums.QuestionSubmitStatusEnum;
import com.lvji.lvjiojbackendmodel.vo.QuestionSubmitVO;
import com.lvji.lvjiojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.lvji.lvjiojbackendquestionservice.message.MyMessageProducer;
import com.lvji.lvjiojbackendquestionservice.service.QuestionService;
import com.lvji.lvjiojbackendquestionservice.service.QuestionSubmitService;
import com.lvji.lvjiojbackendserviceclient.service.JudgeFeignClient;
import com.lvji.lvjiojbackendserviceclient.service.UserFeignClient;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
* @author 常俊杰
* @description 针对表【question_submit(题目提交)】的数据库操作Service实现
* @createDate 2024-03-30 20:03:00
*/
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;


    @Resource
    @Lazy
    private JudgeFeignClient judgeFeignClient;

    @Resource
    private MyMessageProducer myMessageProducer;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        //校验编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if(languageEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"编程语言错误,没有该编程语言");
        }
        long questionId = questionSubmitAddRequest.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        // 提交题目,向数据库插入记录
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setUserId(userId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if(!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"提交题目失败");
        }
        Long questionSubmitId = questionSubmit.getId();
       /* // 异步执行判题服务
        CompletableFuture.runAsync(() -> {
            judgeFeignClient.doJudge(questionSubmitId);
        });*/
        // 生产者向消息队列投放消息
        myMessageProducer.sendMessage("code_exchange","my_routingKey",String.valueOf(questionSubmitId));
        return questionSubmitId;
    }

    /**
     * 获取查询包装类
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();
        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null,"status",status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 单条题目提交信息脱敏
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        Long loginUserId = loginUser.getId();
        // 题目提交信息脱敏 -> 非管理员、非本人无权查看题目提交信息中的提交代码
        if(loginUserId != questionSubmit.getUserId() && !userFeignClient.isAdmin(loginUser)){
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    /**
     * 多条题目提交信息脱敏,并进行分页
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollUtil.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(questionSubmit ->
                getQuestionSubmitVO(questionSubmit, loginUser)
        ).collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }
}




