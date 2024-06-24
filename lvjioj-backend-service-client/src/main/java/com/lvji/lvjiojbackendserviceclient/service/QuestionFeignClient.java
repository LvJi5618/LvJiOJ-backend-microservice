package com.lvji.lvjiojbackendserviceclient.service;

import com.lvji.lvjiojbackendmodel.entity.Question;
import com.lvji.lvjiojbackendmodel.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 题目服务
* @author 常俊杰
* @description 针对表【question(题目)】的数据库操作Service
* @createDate 2024-03-30 20:02:46
*/
@FeignClient(name = "lvjioj-backend-question-service",path = "/api/question/inner")
public interface QuestionFeignClient {

    /**
     * 根据 questionId 获取题目信息
     * @param questionId
     * @return
     */
    @GetMapping("/get/id")
    Question getQuestionById(@RequestParam("questionId") long questionId);

    /**
     * 根据 questionSubmitId 获取题目提交信息
     * @param questionSubmitId
     * @return
     */
    @GetMapping("/question_submit/get/id")
    QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId);

    /**
     * 修改题目提交信息
     * @param questionSubmitUpdate
     * @return
     */
    @PostMapping("/question_submit/update")
    boolean updateQuestionSubmit(@RequestBody QuestionSubmit questionSubmitUpdate);

}
