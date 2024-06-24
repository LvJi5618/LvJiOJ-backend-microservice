package com.lvji.lvjiojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lvji.lvjiojbackendmodel.dto.questionsubmit.QuestionSubmitAddRequest;
import com.lvji.lvjiojbackendmodel.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.lvji.lvjiojbackendmodel.entity.QuestionSubmit;
import com.lvji.lvjiojbackendmodel.entity.User;
import com.lvji.lvjiojbackendmodel.vo.QuestionSubmitVO;

/**
 * @author 常俊杰
 * @description 针对表【question_submit(题目提交)】的数据库操作Service
 * @createDate 2024-03-30 20:03:00
 */
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取题目提交信息封装对象
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目提交信息封装对象
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);
}
