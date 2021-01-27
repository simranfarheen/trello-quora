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
public class AnswerController {

    @Autowired
    AnswerService answerService;

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public HttpEntity<? extends Object> addAnswer(@RequestHeader("authorization") final String accessToken, final AnswerRequest answerRequest, @PathVariable("questionId") String questionId) {

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
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }catch(UserNotFoundException e){
            ErrorResponse errorResponse = new ErrorResponse().code("USR-001").message("This username does not exist");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
  
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUser(userEntity);
        ZonedDateTime now = ZonedDateTime.now();
        answerEntity.setDate(now);
        answerEntity.setAnswer(answerRequest.getAnswer());
        answerEntity.setUuid(userEntity.getUuid());
        try {
            answerEntity.setQuestion(questionService.getQuestionsByUuid(questionId));
            answerService.createAnswer(answerEntity);
        }catch (InvalidQuestionException e){
            ErrorResponse errorResponse = new ErrorResponse().code("QUES-001").message("Entered question uuid does not exist");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("Answer created");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public HttpEntity<? extends Object>  editAnswer(final AnswerEditRequest answerEditRequest, @PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String accessToken)  {
        UserEntity userEntity;
        UserAuthTokenEntity userAuthTokenEntity;
        AnswerEntity answerEntity;
        try {
            userEntity = userService.checkIfUserExists(accessToken);
            userAuthTokenEntity = userService.checkIfUserLoggedIn(accessToken);
            answerEntity = answerService.editAnswer(userEntity, answerId, answerEditRequest.getContent());

        } catch(AuthenticationFailedException e){
            ErrorResponse errorResponse;
            if(e.getCode().equalsIgnoreCase("ATHR-001")) {
                errorResponse = new ErrorResponse().code("ATHR-001").message("This username does not exist");
            }else{
                errorResponse = new ErrorResponse().code("ATHR-002").message("Password Failed");
            }
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }catch(UserNotFoundException e){
            ErrorResponse errorResponse = new ErrorResponse().code("USR-001").message("This username does not exist");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch(AuthorizationFailedException e){
            ErrorResponse errorResponse = new ErrorResponse().code("ATHR-003").message("Only the question owner can edit the question");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        } catch(AnswerNotFoundException e){
            ErrorResponse errorResponse = new ErrorResponse().code("ANS-001").message("Entered answer uuid does not exist");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
  
        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(answerEntity.getUuid()).status("Answer Edited");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }
  
    @RequestMapping(method = RequestMethod.DELETE, path ="/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public HttpEntity<? extends Object>  deleteQuestion(@PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String accessToken)  {
        UserEntity userEntity;
        UserAuthTokenEntity userAuthTokenEntity;
        AnswerEntity answerEntity;
        try {
            userEntity = userService.checkIfUserExists(accessToken);
            userAuthTokenEntity = userService.checkIfUserLoggedIn(accessToken);
            answerEntity = answerService.deleteAnswer(answerId, userEntity, userAuthTokenEntity);

        } catch(AuthenticationFailedException e){
            ErrorResponse errorResponse;
            if(e.getCode().equalsIgnoreCase("ATHR-001")) {
                errorResponse = new ErrorResponse().code("ATHR-001").message("This username does not exist");
            }else{
                errorResponse = new ErrorResponse().code("ATHR-002").message("Password Failed");
            }
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }catch(UserNotFoundException e){
            ErrorResponse errorResponse = new ErrorResponse().code("USR-001").message("This username does not exist");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch(AnswerNotFoundException e){
            ErrorResponse errorResponse = new ErrorResponse().code("ANS-001").message("Entered answer uuid does not exist");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }catch(AuthorizationFailedException e){
            ErrorResponse errorResponse = new ErrorResponse().code("ATHR-003").message("Only the question owner can edit the question");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }

        AnswerDeleteResponse answerDeleteResponse  = new AnswerDeleteResponse().id(answerEntity.getUuid()).status("Answer Deleted");
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET, path = "/answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public HttpEntity<? extends Object> showQuestionsByUser(@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String accessToken) {
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
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }catch(UserNotFoundException e){
            ErrorResponse errorResponse = new ErrorResponse().code("USR-001").message("This username does not exist");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        List<AnswerEntity> answerEntities;

        try {
            answerEntities = answerService.getAllAnswers(questionId);
        }catch (InvalidQuestionException e){
            ErrorResponse errorResponse = new ErrorResponse().code("QUES-001").message("Entered question uuid does not exist");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        List<AnswerDetailsResponse> answerDetailsResponses = new ArrayList<>();
        for (AnswerEntity answerEntity : answerEntities) {
            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse().id(answerEntity.getUuid()).answerContent(answerEntity.getAnswer()).questionContent(answerEntity.getQuestion().getContent());
            answerDetailsResponses.add(answerDetailsResponse);
        }

        return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponses, HttpStatus.OK);
    }
}
