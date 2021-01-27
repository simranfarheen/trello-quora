package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.QuestionEntity;
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
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @RequestMapping(method = RequestMethod.POST, path = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public HttpEntity<? extends Object> createQuestion(@RequestHeader("authorization") final String accessToken, final QuestionRequest questionRequest) throws AuthenticationFailedException, UserNotFoundException {

        UserEntity userEntity;
        UserAuthTokenEntity userAuthTokenEntity;

        try {
         userEntity = userService.checkIfUserExists(accessToken);
         userAuthTokenEntity = userService.checkIfUserLoggedIn(accessToken);
        } catch(AuthenticationFailedException e){
            ErrorResponse errorResponse = new ErrorResponse().code("ATHR-002").message("Password Failed");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }catch(UserNotFoundException e){
            ErrorResponse errorResponse = new ErrorResponse().code("ATHR-001").message("This username does not exist");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }

        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUser(userEntity);
        questionEntity.setContent(questionRequest.getContent());
        ZonedDateTime now = ZonedDateTime.now();
        questionEntity.setDate(now);
        questionEntity.setUuid(userEntity.getUuid());

        questionService.createQuestion(questionEntity);

        QuestionResponse questionResponse = new QuestionResponse().id(questionEntity.getUuid()).status("Question created");
        return new ResponseEntity<>(questionResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public  HttpEntity<? extends Object>  showAllQuestions(@RequestHeader("authorization") final String accessToken) throws UserNotFoundException, AuthenticationFailedException {

        UserEntity userEntity;
        UserAuthTokenEntity userAuthTokenEntity;

        try {
            userEntity = userService.checkIfUserExists(accessToken);
            userAuthTokenEntity = userService.checkIfUserLoggedIn(accessToken);
        } catch(AuthenticationFailedException e){
                ErrorResponse errorResponse;
                if(e.getCode().equalsIgnoreCase("ATHR-001")) {
                    errorResponse = new ErrorResponse().code("ATHR-001").message("This username does not exist");
                }else{
                    errorResponse = new ErrorResponse().code("ATHR-002").message("Password Failed");
                }
                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
            } catch(UserNotFoundException e){
                ErrorResponse errorResponse = new ErrorResponse().code("USR-001").message("User with entered uuid does not exist");
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            }

        List<QuestionEntity> questionEntityList = questionService.getAllQuestions();

        List<QuestionDetailsResponse> questionResponseList = new ArrayList<>();
        for (QuestionEntity questionEntity : questionEntityList) {
            QuestionDetailsResponse questionResponse = new QuestionDetailsResponse().id(questionEntity.getUuid()).content(questionEntity.getContent());
            questionResponseList.add(questionResponse);
        }

        return new ResponseEntity<>(questionResponseList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/edit/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public HttpEntity<? extends Object>   editQuestion(final QuestionEditRequest questionEditRequest, @PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String accessToken) throws AuthenticationFailedException, UserNotFoundException, AuthorizationFailedException, InvalidQuestionException {

        UserEntity userEntity;
        UserAuthTokenEntity userAuthTokenEntity;

        try {
            userEntity = userService.checkIfUserExists(accessToken);
            userAuthTokenEntity = userService.checkIfUserLoggedIn(accessToken);
        } catch(AuthenticationFailedException e){
            ErrorResponse errorResponse = new ErrorResponse().code("ATHR-002").message("Password Failed");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }catch(UserNotFoundException e){
            ErrorResponse errorResponse = new ErrorResponse().code("ATHR-001").message("This username does not exist");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }

        QuestionEntity questionEntity;

        try {
             questionEntity = questionService.editQuestion(userEntity, questionId, questionEditRequest.getContent());
        }catch(InvalidQuestionException e){
            ErrorResponse errorResponse = new ErrorResponse().code("QUES-001").message("Entered question uuid does not exist");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }catch(AuthorizationFailedException e){
            ErrorResponse errorResponse = new ErrorResponse().code("ATHR-003").message("Only the question owner can edit the question");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }

        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(questionEntity.getUuid()).status("Question Edited");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path ="/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public HttpEntity<? extends Object>   deleteQuestion(@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String accessToken) throws UserNotFoundException, AuthenticationFailedException, InvalidQuestionException {

        UserEntity userEntity;
        UserAuthTokenEntity userAuthTokenEntity;

        try {
            userEntity = userService.checkIfUserExists(accessToken);
            userAuthTokenEntity = userService.checkIfUserLoggedIn(accessToken);
        } catch(AuthenticationFailedException e){
            ErrorResponse errorResponse = new ErrorResponse().code("ATHR-002").message("Password Failed");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }catch(UserNotFoundException e){
            ErrorResponse errorResponse = new ErrorResponse().code("ATHR-001").message("This username does not exist");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }

        QuestionEntity questionEntity;

        try {
             questionEntity = questionService.deleteQuestion(questionId, userEntity);
        }catch(InvalidQuestionException e){
            ErrorResponse errorResponse = new ErrorResponse().code("QUES-001").message("Entered question uuid does not exist");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch(AuthorizationFailedException e){
            ErrorResponse errorResponse = new ErrorResponse().code("ATHR-003").message("Only the question owner can edit the question");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }

        QuestionDeleteResponse questionDeleteResponse  = new QuestionDeleteResponse().id(questionEntity.getUuid()).status("Question Deleted");

        return new ResponseEntity<>(questionDeleteResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public HttpEntity<? extends Object>  showQuestionsByUser(@PathVariable("userId") final String userId, @RequestHeader("authorization") final String accessToken) throws UserNotFoundException, AuthenticationFailedException {

        UserEntity userEntity;
        UserAuthTokenEntity userAuthTokenEntity;

        try {
            userEntity = userService.checkIfUserExists(accessToken);
            userAuthTokenEntity = userService.checkIfUserLoggedIn(accessToken);
            userService.getUser(userId, accessToken);
        }catch(AuthenticationFailedException e){
            ErrorResponse errorResponse;
            if(e.getCode().equalsIgnoreCase("ATHR-001")) {
                errorResponse = new ErrorResponse().code("ATHR-001").message("This username does not exist");
            }else{
                errorResponse = new ErrorResponse().code("ATHR-002").message("Password Failed");
            }
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        } catch(UserNotFoundException e){
            ErrorResponse errorResponse = new ErrorResponse().code("USR-001").message("User with entered uuid does not exist");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }


        List<QuestionEntity> questionEntities = questionService.getQuestionsByUser(userId);

        List<QuestionDetailsResponse> questionResponseList = new ArrayList<>();
        for (QuestionEntity questionEntity : questionEntities) {
            QuestionDetailsResponse questionResponse = new QuestionDetailsResponse().id(questionEntity.getUuid()).content(questionEntity.getContent());
            questionResponseList.add(questionResponse);
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(questionResponseList, HttpStatus.OK);
    }
}
