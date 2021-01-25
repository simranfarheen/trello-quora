package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @RequestMapping(method = RequestMethod.POST, path = "/create")
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("access-token") final String accessToken, final QuestionRequest questionRequest) throws AuthenticationFailedException, UserNotFoundException {

        UserEntity userEntity = userService.checkIfUserExists(accessToken);
        UserAuthTokenEntity userAuthTokenEntity = userService.checkIfUserLoggedIn(accessToken);
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUser(userEntity);
        questionEntity.setContent(questionRequest.getContent());
        ZonedDateTime now = ZonedDateTime.now();
        questionEntity.setDate(now);
        questionEntity.setUuid(userEntity.getUuid());
        questionService.createQuestion(questionEntity);
        QuestionResponse questionResponse = new QuestionResponse().id(questionEntity.getUuid()).status("Question created");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/all")
    public  ResponseEntity<QuestionDetailsResponse> showAllQuestions(){
        return null;
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/edit/{questionId}")
    public ResponseEntity<QuestionEditResponse>  editQuestion(){
        return null;
    }

    @RequestMapping(method = RequestMethod.DELETE, path ="/delete/{questionId}")
    public ResponseEntity<QuestionDeleteResponse>  deleteQuestion(){
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/all/{userId}")
    public ResponseEntity<QuestionDetailsResponse>  showQuestionsByUser(){
        return null;
    }
}
