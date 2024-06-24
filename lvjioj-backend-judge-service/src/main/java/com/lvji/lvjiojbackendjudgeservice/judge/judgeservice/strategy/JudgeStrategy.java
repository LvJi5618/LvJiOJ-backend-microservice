package com.lvji.lvjiojbackendjudgeservice.judge.judgeservice.strategy;

import com.lvji.lvjiojbackendmodel.codesandbox.JudgeInfo;

/**
 * 判题策略
 */
public interface JudgeStrategy {

    JudgeInfo doJudge(JudgeContext judgeContext);
}
