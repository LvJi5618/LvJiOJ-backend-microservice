package com.lvji.lvjiojbackendjudgeservice.judge.judgeservice.strategy;

import com.lvji.lvjiojbackendmodel.codesandbox.JudgeInfo;
import com.lvji.lvjiojbackendmodel.dto.question.JudgeCase;
import com.lvji.lvjiojbackendmodel.dto.question.JudgeConfig;
import com.lvji.lvjiojbackendmodel.enums.QuestionSubmitJudgeInfoMessageEnum;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DefaultJudgeStrategy implements JudgeStrategy{

    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext){
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<JudgeCase> judgeCases = judgeContext.getJudgeCases();
        List<String> actualOutputList = judgeContext.getActualOutputList();
        List<String> expectedOutputList = judgeCases.stream().map(JudgeCase::getOutput).collect(Collectors.toList());
        JudgeConfig judgeConfig = judgeContext.getJudgeConfig();
        QuestionSubmitJudgeInfoMessageEnum judgeInfoMessageEnum = QuestionSubmitJudgeInfoMessageEnum.ACCEPTED;
        JudgeInfo judgeInfoResult = new JudgeInfo();
        judgeInfoResult.setMemory(Optional.ofNullable(judgeInfo.getMemory()).orElse(0L));
        judgeInfoResult.setTime(judgeInfo.getTime());
        // 判题策略一:判断沙箱执行的结果输出数量和预取输出数量是否相等
        if(actualOutputList.size() != expectedOutputList.size()){
            judgeInfoMessageEnum = QuestionSubmitJudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResult.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResult;
        }
        // 判题策略二：依次判断每一项输出用例和预期输出是否相等
        for (int i = 0; i < judgeCases.size(); i++) {
            String expectedOutput = expectedOutputList.get(i);
            String actualOutput = actualOutputList.get(i);
            if(!expectedOutput.equals(actualOutput)){
                judgeInfoMessageEnum = QuestionSubmitJudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResult.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResult;
            }
        }
        // 判题策略三:判题题目的限制是否符合要求。如：内存限制、时间限制
        if(judgeConfig.getMemoryLimit() > Optional.ofNullable(judgeInfo.getMemory()).orElse(0L)){
            judgeInfoMessageEnum = QuestionSubmitJudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResult.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResult;
        }
        if(judgeConfig.getTimeLimit() > judgeInfo.getTime()){
            judgeInfoMessageEnum = QuestionSubmitJudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResult.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResult;
        }
        judgeInfoResult.setMessage(judgeInfoMessageEnum.getValue());
        return judgeInfoResult;
    }
}
