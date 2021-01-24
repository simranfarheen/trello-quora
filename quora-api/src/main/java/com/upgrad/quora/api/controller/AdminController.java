package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    AdminService adminService;
    //TODO: takes userID of the user to be deleted and accessToken of the signed in user
    //TODO: if accessToken does not exist throw AuthorizationFailedException
    //TODO: if user is signed out throw AuthorizationFailedException
    //TODO: if user is nonadmin throw AuthorizationFailedException
    //TODO: if user to be deleted doesnt exist throw UserNotFoundException
    //TODO: else delete all records from table related to user and return UUID of user
    @RequestMapping(method = RequestMethod.DELETE, path = "/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity<UserDeleteResponse> deleteUser(@PathVariable("userId") String userId, @RequestHeader("access-token") String accessToken) throws AuthorizationFailedException, UserNotFoundException {

        UserEntity userEntity = adminService.deleteUser(accessToken, userId);
        UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id(userEntity.getUuid()).status("User successfully deleted");

        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse, HttpStatus.OK);
    }
}
