package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    AdminService adminService;
    @RequestMapping(method = RequestMethod.DELETE, path = "/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    HttpEntity<? extends Object> deleteUser(@PathVariable("userId") String userId, @RequestHeader("authorization") String accessToken) throws AuthorizationFailedException, UserNotFoundException {
        UserEntity userEntity = null;

        try {
            userEntity = adminService.deleteUser(accessToken, userId);
        }catch (AuthorizationFailedException e){
            if(e.getCode().equalsIgnoreCase("ATHR-001")){
                ErrorResponse errorResponse = new ErrorResponse().code("ATHR-001").message("User has not signed in");
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            }
            if(e.getCode().equalsIgnoreCase("ATHR-002")){
                ErrorResponse errorResponse = new ErrorResponse().code("ATHR-002").message("User is signed out");
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            }
            if(e.getCode().equalsIgnoreCase("ATHR-003")){
                ErrorResponse errorResponse = new ErrorResponse().code("ATHR-003").message("Unauthorized Access, Entered user is not an admin");
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            }
        }catch(UserNotFoundException e){
            ErrorResponse errorResponse = new ErrorResponse().code("USR-001").message("User with entered uuid to be deleted does not exist");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id(userEntity.getUuid()).status("User successfully deleted");

        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse, HttpStatus.OK);
    }
}
