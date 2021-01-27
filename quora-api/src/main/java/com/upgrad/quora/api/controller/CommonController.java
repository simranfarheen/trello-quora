package com.upgrad.quora.api.controller;

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
    public HttpEntity<? extends Object> getUserProfileByUserId(@RequestHeader("authorization") final String accessToken, @PathVariable("userId") String userId) throws AuthenticationFailedException, UserNotFoundException {

        final UserEntity userEntity = userService.getUser(userId, accessToken);
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().userName(userEntity.getUsername())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .emailAddress(userEntity.getEmail())
                .contactNumber(userEntity.getContactNumber())
                .country(userEntity.getCountry())
                .dob(userEntity.getDob())
                .aboutMe(userEntity.getAboutMe());

        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }
}
