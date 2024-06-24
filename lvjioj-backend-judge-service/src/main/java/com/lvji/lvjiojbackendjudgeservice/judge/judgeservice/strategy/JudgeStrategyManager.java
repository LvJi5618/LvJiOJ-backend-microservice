package com.lvji.lvjiojbackendjudgeservice.judge.judgeservice.strategy;

import com.lvji.lvjiojbackendmodel.codesandbox.JudgeInfo;
import org.springframework.stereotype.Service;

/**
 * 判题策略管理（简化调用）
 */
@Service
public class JudgeStrategyManager {

    /**
     * 执行判题
     * @param judgeContext
     * @param language
     * @return
     */
    public JudgeInfo doJudge(JudgeContext judgeContext, String language){
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        /*if(QuestionSubmitLanguageEnum.JAVA.getValue().equals(language)){
            strategy = new JavaLanguageJudgeStrategy();
        }*/
        return judgeStrategy.doJudge(judgeContext);
    }

}
