package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.upgrad.quora.service.exception.AuthorizationFailedException;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UserService userService;

    /**
     * Controller Method to view User Profile details based on the User UUID
     * @param userId : UUID of the user
     * @param authorization : Acess Token generated during user Login.
     * @return UserDetailsResponse : Models all the user profile details
     * @throws AuthenticationFailedException : if AUTh token is invalid or not active
     * @throws UserNotFoundException : if UUID of the user is invalid
     * @author Prabhjot
     */

    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUserProfileById(@PathVariable("userId") final String userId, @RequestHeader("authorization") final String authorization) throws UserNotFoundException, AuthenticationFailedException {


        UserEntity userById = userService.getUser(userId, authorization);

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();

        userDetailsResponse.setFirstName(userById.getFirstName());
        userDetailsResponse.setLastName(userById.getLastName());
        userDetailsResponse.setUserName(userById.getUsername());
        userDetailsResponse.setEmailAddress(userById.getEmail());
        userDetailsResponse.setCountry(userById.getCountry());
        userDetailsResponse.setAboutMe(userById.getAboutMe());
        userDetailsResponse.setContactNumber(userById.getContactNumber());
        userDetailsResponse.setDob(userById.getDob());

        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK) ;

    }
}
