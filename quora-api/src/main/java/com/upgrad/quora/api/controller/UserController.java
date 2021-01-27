package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(method = RequestMethod.POST, path ="/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public HttpEntity<? extends Object> signup(final SignupUserRequest signupUserRequest)  {

        final UserEntity userEntity = new UserEntity();

        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUsername(signupUserRequest.getUserName());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setContactNumber(signupUserRequest.getContactNumber());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setRole("nonadmin");

        final UserEntity createdUserEntity;
        try {
             createdUserEntity = userService.signup(userEntity);
        } catch(SignUpRestrictedException e){
            if(e.getCode().equalsIgnoreCase("SGR-001")) {
                ErrorResponse errorResponse = new ErrorResponse().code("SGR-001").message("Try any other Username, this Username has already been taken");
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            }else{
                ErrorResponse errorResponse = new ErrorResponse().code("SGR-002").message("This user has already been registered, try with any other emailId");
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            }
        }

        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("User Successfully Registered");
        return new ResponseEntity<>(userResponse,HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public  HttpEntity<? extends Object> signin(@RequestHeader("authorization") final String authorization) {


        byte[] decode = Base64.getMimeDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        UserAuthTokenEntity userAuthToken;

        try {
            userAuthToken = authenticationService.authenticate(decodedArray[0], decodedArray[1]);
        } catch(AuthenticationFailedException e){
            ErrorResponse errorResponse;
            if(e.getCode().equalsIgnoreCase("ATH-001")) {
                errorResponse = new ErrorResponse().code("ATH-001").message("This username does not exist");
            }else{
                errorResponse = new ErrorResponse().code("ATH-002").message("Password Failed");
            }
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        UserEntity user = userAuthToken.getUser();

        SigninResponse authorizedUserResponse = new SigninResponse().id(user.getUuid()).message("Signed in Successfully");

        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthToken.getAccessToken());
        return new ResponseEntity<>(authorizedUserResponse, headers, HttpStatus.OK);
    }

    //TODO: Must request access token of the signed in user
    //TODO: If access_token does not exist, throw SignOutRestrictedException
    //TODO: If access_token is valid, update logout time in DB and return uuid of the signed out user

    @RequestMapping(method = RequestMethod.POST, path = "/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public HttpEntity<? extends Object> signout(@RequestHeader("authorization") final String accessToken) {

        UserAuthTokenEntity userAuthToken;
        try {
              userAuthToken = authenticationService.logout(accessToken);
        }catch(AuthenticationFailedException e){
            ErrorResponse errorResponse = new ErrorResponse().code("SGR-001").message("User is not Signed in");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        UserEntity user = userAuthToken.getUser();

        SigninResponse authorizedUserResponse = new SigninResponse().id(user.getUuid()).message("Signed out Successfully");
        return new ResponseEntity<SigninResponse>(authorizedUserResponse, HttpStatus.OK);
    }


}
