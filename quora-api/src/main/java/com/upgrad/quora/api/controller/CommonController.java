package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UserService userService;

    //TODO: request userID and access token of a signed in user
    //TODO: if access token does not exist throw AuthorizationFailedException exception
    //TODO: if user has signed out, throw AuthorizationFailedException exception
    //TODO: if user with uuid does not exist in db throw UserNotFoundException exception
    //TODO: return details of user from DB

    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public HttpEntity<? extends Object> getUserProfileByUserId(@RequestHeader("authorization") final String accessToken, @PathVariable("userId") String userId) {
        final UserEntity userEntity;

        try{
             userEntity = userService.getUser(userId, accessToken);
        }catch(AuthenticationFailedException e){
            if(e.getCode().equalsIgnoreCase("ATHR-002")) {
                ErrorResponse errorResponse = new ErrorResponse().code("ATHR-002").message("User is signed out.Sign in first to get user details");
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            }else{
                ErrorResponse errorResponse = new ErrorResponse().code("ATHR-001").message("User has not signed in");
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            }
        }catch(UserNotFoundException e){
            ErrorResponse errorResponse = new ErrorResponse().code("USR-001").message("User with entered uuid does not exist");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().userName(userEntity.getUsername())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .emailAddress(userEntity.getEmail())
                .contactNumber(userEntity.getContactNumber())
                .country(userEntity.getCountry())
                .dob(userEntity.getDob())
                .aboutMe(userEntity.getAboutMe());

        return new ResponseEntity<>(userDetailsResponse, HttpStatus.OK);
    }
}
