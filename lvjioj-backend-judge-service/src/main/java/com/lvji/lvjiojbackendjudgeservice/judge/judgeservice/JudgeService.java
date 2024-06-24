package com.lvji.lvjiojbackendjudgeservice.judge.judgeservice;

import com.lvji.lvjiojbackendmodel.entity.QuestionSubmit;

/**
 * 判题服务接口
 */
public interface JudgeService {

    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(long questionSubmitId);

}
