package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @RequestMapping(method = RequestMethod.POST, path = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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

    @RequestMapping(method = RequestMethod.GET, path = "/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public  ResponseEntity<List<QuestionDetailsResponse>> showAllQuestions(@RequestHeader("access-token") final String accessToken) throws UserNotFoundException, AuthenticationFailedException {
        UserEntity userEntity = userService.checkIfUserExists(accessToken);
        UserAuthTokenEntity userAuthTokenEntity = userService.checkIfUserLoggedIn(accessToken);
        List<QuestionEntity> questionEntityList = questionService.getAllQuestions();
        List<QuestionDetailsResponse> questionResponseList = new ArrayList<>();
        for (QuestionEntity questionEntity : questionEntityList) {
            QuestionDetailsResponse questionResponse = new QuestionDetailsResponse().id(questionEntity.getUuid()).content(questionEntity.getContent());
            questionResponseList.add(questionResponse);
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(questionResponseList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/edit/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse>  editQuestion(final QuestionEditRequest questionEditRequest, @PathVariable("questionId") final String questionId, @RequestHeader("access-token") final String accessToken) throws AuthenticationFailedException, UserNotFoundException, AuthorizationFailedException, InvalidQuestionException {
        UserEntity userEntity = userService.checkIfUserExists(accessToken);
        UserAuthTokenEntity userAuthTokenEntity = userService.checkIfUserLoggedIn(accessToken);
        QuestionEntity questionEntity = questionService.editQuestion(userEntity, questionId, questionEditRequest.getContent());
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(questionEntity.getUuid()).status("Question Edited");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path ="/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse>  deleteQuestion(@PathVariable("questionId") final String questionId, @RequestHeader("access-token") final String accessToken) throws UserNotFoundException, AuthenticationFailedException, InvalidQuestionException {
        UserEntity userEntity =userService.checkIfUserExists(accessToken);
        UserAuthTokenEntity userAuthTokenEntity =userService.checkIfUserLoggedIn(accessToken);
        QuestionEntity questionEntity = questionService.deleteQuestion(questionId);

        QuestionDeleteResponse questionDeleteResponse  = new QuestionDeleteResponse().id(questionEntity.getUuid()).status("Question Deleted");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> showQuestionsByUser(@PathVariable("userId") final String userId, @RequestHeader("access-token") final String accessToken) throws UserNotFoundException, AuthenticationFailedException {
        UserEntity userEntity =userService.checkIfUserExists(accessToken);
        UserAuthTokenEntity userAuthTokenEntity =userService.checkIfUserLoggedIn(accessToken);
        List<QuestionEntity> questionEntities = questionService.getQuestionsByUser(userId);

        List<QuestionDetailsResponse> questionResponseList = new ArrayList<>();
        for (QuestionEntity questionEntity : questionEntities) {
            QuestionDetailsResponse questionResponse = new QuestionDetailsResponse().id(questionEntity.getUuid()).content(questionEntity.getContent());
            questionResponseList.add(questionResponse);
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(questionResponseList, HttpStatus.OK);
    }
}
