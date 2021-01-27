package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/answer")
public class AnswerController {

    @Autowired
    AnswerService answerService;

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @RequestMapping(method = RequestMethod.POST, path = "/create/question/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public HttpEntity<? extends Object> addAnswer(@RequestHeader("authorization") final String accessToken, final AnswerRequest answerRequest, @PathVariable("questionId") String questionId) throws AuthenticationFailedException, UserNotFoundException {

        UserEntity userEntity = userService.checkIfUserExists(accessToken);
        UserAuthTokenEntity userAuthTokenEntity = userService.checkIfUserLoggedIn(accessToken);
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUser(userEntity);
        ZonedDateTime now = ZonedDateTime.now();
        answerEntity.setDate(now);
        answerEntity.setAnswer(answerRequest.getAnswer());
        answerEntity.setUuid(userEntity.getUuid());
        answerEntity.setQuestion(questionService.getQuestionsByUuid(questionId));
        answerService.createAnswer(answerEntity);
        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("Answer created");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/edit/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public HttpEntity<? extends Object>  editAnswer(final AnswerEditRequest answerEditRequest, @PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String accessToken) throws AuthenticationFailedException, UserNotFoundException, AuthorizationFailedException, InvalidQuestionException, AnswerNotFoundException {
        UserEntity userEntity = userService.checkIfUserExists(accessToken);
        UserAuthTokenEntity userAuthTokenEntity = userService.checkIfUserLoggedIn(accessToken);
        AnswerEntity answerEntity = answerService.editAnswer(userEntity, answerId, answerEditRequest.getContent());
        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(answerEntity.getUuid()).status("Answer Edited");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path ="/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public HttpEntity<? extends Object>  deleteQuestion(@PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String accessToken) throws UserNotFoundException, AuthenticationFailedException, InvalidQuestionException, AnswerNotFoundException, AuthorizationFailedException {
        UserEntity userEntity =userService.checkIfUserExists(accessToken);
        UserAuthTokenEntity userAuthTokenEntity =userService.checkIfUserLoggedIn(accessToken);
        AnswerEntity answerEntity = answerService.deleteAnswer(answerId,userEntity,userAuthTokenEntity);

        AnswerDeleteResponse answerDeleteResponse  = new AnswerDeleteResponse().id(answerEntity.getUuid()).status("Answer Deleted");
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public HttpEntity<? extends Object> showQuestionsByUser(@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String accessToken) throws UserNotFoundException, AuthenticationFailedException, InvalidQuestionException {
        UserEntity userEntity =userService.checkIfUserExists(accessToken);
        UserAuthTokenEntity userAuthTokenEntity =userService.checkIfUserLoggedIn(accessToken);
        List<AnswerEntity> answerEntities = answerService.getAllAnswers(questionId);

        List<AnswerDetailsResponse> answerDetailsResponses = new ArrayList<>();
        for (AnswerEntity answerEntity : answerEntities) {
            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse().id(answerEntity.getUuid()).answerContent(answerEntity.getAnswer()).questionContent(answerEntity.getQuestion().getContent());
            answerDetailsResponses.add(answerDetailsResponse);
        }

        return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponses, HttpStatus.OK);
    }
}
