package com.lvji.lvjiojbackendjudgeservice.judge.judgeservice.impl;

import cn.hutool.json.JSONUtil;
import com.lvji.lvjiojbackendcommon.common.ErrorCode;
import com.lvji.lvjiojbackendcommon.exception.BusinessException;
import com.lvji.lvjiojbackendjudgeservice.judge.codesandboxservice.CodeSandBoxFactory;
import com.lvji.lvjiojbackendjudgeservice.judge.codesandboxservice.CodeSandBoxProxy;
import com.lvji.lvjiojbackendjudgeservice.judge.judgeservice.JudgeService;
import com.lvji.lvjiojbackendjudgeservice.judge.judgeservice.strategy.JudgeContext;
import com.lvji.lvjiojbackendjudgeservice.judge.judgeservice.strategy.JudgeStrategyManager;
import com.lvji.lvjiojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.lvji.lvjiojbackendmodel.codesandbox.ExecuteCodeResponse;
import com.lvji.lvjiojbackendmodel.codesandbox.JudgeInfo;
import com.lvji.lvjiojbackendmodel.dto.question.JudgeCase;
import com.lvji.lvjiojbackendmodel.dto.question.JudgeConfig;
import com.lvji.lvjiojbackendmodel.entity.Question;
import com.lvji.lvjiojbackendmodel.entity.QuestionSubmit;
import com.lvji.lvjiojbackendmodel.enums.QuestionSubmitStatusEnum;
import com.lvji.lvjiojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    QuestionFeignClient questionFeignClient;

    @Value("${codesandbox.type}")
    private String type;

    @Resource
    JudgeStrategyManager judgeStrategyManager;

    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        /**
         * 1. 从question_submit表中，获取到对应的题目id、用户代码执行信息（JudgeInfo）
         */
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if(questionSubmit == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"题目提交信息不存在");
        }
        String language = questionSubmit.getLanguage();
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getQuestionById(questionId);
        if(question == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"题目不存在");
        }
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        String judgeInfoStr = questionSubmit.getJudgeInfo();
        JudgeInfo judgeInfo = JSONUtil.toBean(judgeInfoStr, JudgeInfo.class);
        /**
         * 2. 若题目提交状态不为 “等待中” ，无需重复执行判题服务
         */
        Integer status = questionSubmit.getStatus();
        if(QuestionSubmitStatusEnum.WAITING.getValue() != status){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"该题目正在判题中,无需重复提交");
        }
        /**
         * 3. 修改题目提交状态为“判题中”，防止重复执行判题服务
         */
        status = QuestionSubmitStatusEnum.RUNNING.getValue();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(status);
        boolean update = questionFeignClient.updateQuestionSubmit(questionSubmitUpdate);
        if(!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        /**
         * 4. 调用代码沙箱服务，获取到沙箱执行结果
         */
        CodeSandBoxProxy codeSandBoxProxy = new CodeSandBoxProxy(CodeSandBoxFactory.newInstance(type));
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCases = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCases.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder().
                code(questionSubmit.getCode()).
                language(language).
                inputList(inputList).
                build();
        ExecuteCodeResponse executeCodeResponse = codeSandBoxProxy.executeCode(executeCodeRequest);
        List<String> actualOutputList = executeCodeResponse.getOutputList();
        /**
         * 5. 根据设定的判题策略,判断代码沙箱执行结果
         */
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeCases(judgeCases);
        judgeContext.setActualOutputList(actualOutputList);
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setJudgeConfig(judgeConfig);
        JudgeInfo judgeInfoResult = judgeStrategyManager.doJudge(judgeContext, language);
        /**
         * 6. 设置题目的判题状态和判题执行信息
          */
        status = QuestionSubmitStatusEnum.SUCCEED.getValue();
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(status);
        String judgeInfoResultStr = JSONUtil.toJsonStr(judgeInfoResult);
        questionSubmitUpdate.setJudgeInfo(judgeInfoResultStr);
        update = questionFeignClient.updateQuestionSubmit(questionSubmitUpdate);
        if(!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        QuestionSubmit questionSubmitResult = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        return questionSubmitResult;
    }
}
