package com.lvji.lvjiojbackendjudgeservice.judge.judgeservice.strategy;

import com.lvji.lvjiojbackendmodel.codesandbox.JudgeInfo;
import com.lvji.lvjiojbackendmodel.dto.question.JudgeCase;
import com.lvji.lvjiojbackendmodel.dto.question.JudgeConfig;
import lombok.Data;

import java.util.List;

@Data
public class JudgeContext {

    /**
     * 判题用例
     */
    private List<JudgeCase> judgeCases;

    /**
     * 代码沙箱执行结果
     */
    private List<String> actualOutputList;

    /**
     * 判题执行信息
     */
    private JudgeInfo judgeInfo;

    /**
     * 判题配置
     */
    private JudgeConfig judgeConfig;

}
