
package com.lvji.lvjiojbackendquestionservice.controller.inner;

import com.lvji.lvjiojbackendmodel.entity.Question;
import com.lvji.lvjiojbackendmodel.entity.QuestionSubmit;
import com.lvji.lvjiojbackendquestionservice.service.QuestionService;
import com.lvji.lvjiojbackendquestionservice.service.QuestionSubmitService;
import com.lvji.lvjiojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {

    @Resource
    QuestionService questionService;

    @Resource
    QuestionSubmitService questionSubmitService;

    /**
     * 根据 questionId 获取题目信息
     * @param questionId
     * @return
     */
    @GetMapping("/get/id")
    public Question getQuestionById(@RequestParam("questionId") long questionId){
        return questionService.getById(questionId);
    }

    /**
     * 根据 questionSubmitId 获取题目提交信息
     * @param questionSubmitId
     * @return
     */
    @GetMapping("/question_submit/get/id")
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId){
        return questionSubmitService.getById(questionSubmitId);
    }

    /**
     * 修改题目提交信息
     * @param questionSubmitUpdate
     * @return
     */
    @PostMapping("/question_submit/update")
    public boolean updateQuestionSubmit(@RequestBody QuestionSubmit questionSubmitUpdate){
        return questionSubmitService.updateById(questionSubmitUpdate);
    }
}
