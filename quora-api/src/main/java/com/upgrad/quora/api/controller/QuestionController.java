package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDeleteResponse;
import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionEditResponse;
import com.upgrad.quora.api.model.QuestionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @RequestMapping("/create")
    public ResponseEntity<QuestionResponse> createQuestion(){
        return null;
    }

    @RequestMapping("/all")
    public  ResponseEntity<QuestionDetailsResponse> showAllQuestions(){
        return null;
    }

    @RequestMapping("/edit/{questionId}")
    public ResponseEntity<QuestionEditResponse>  editQuestion(){
        return null;
    }

    @RequestMapping("/delete/{questionId}")
    public ResponseEntity<QuestionDeleteResponse>  deleteQuestion(){
        return null;
    }

    @RequestMapping("/all/{userId}")
    public ResponseEntity<QuestionDetailsResponse>  showQuestionsByUser(){
        return null;
    }
}
