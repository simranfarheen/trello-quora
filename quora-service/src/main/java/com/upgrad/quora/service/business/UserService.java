package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {

        if(userDao.getUserByUsername(userEntity.getUsername())!=null)
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");

        if(userDao.getUserByEmail(userEntity.getEmail())!=null)
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");

        String password = userEntity.getPassword();
        if (password == null) {
            userEntity.setPassword("quora@123");
        }

        String[] encryptedText = cryptographyProvider.encrypt(password);
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);

        return userDao.createUser(userEntity);
    }

    public UserEntity getUser(final String userUUID, final String accessToken) throws AuthenticationFailedException, UserNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        UserEntity userEntity = userDao.getUser(userUUID);

        if(userEntity == null)
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");

        if (userAuthTokenEntity == null) {
            throw new AuthenticationFailedException("ATHR-001", "User has not signed in");
        }

        if(userAuthTokenEntity.getLogoutAt()!=null)
            throw new AuthenticationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");


        return userEntity;

    }

    public UserAuthTokenEntity checkIfUserLoggedIn(final String accessToken) throws UserNotFoundException, AuthenticationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);

        if (userAuthTokenEntity == null) {
            throw new AuthenticationFailedException("ATHR-001", "User has not signed in");
        }

        if(userAuthTokenEntity.getLogoutAt()!=null)
            throw new AuthenticationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");

        return userAuthTokenEntity;
    }

    public UserEntity checkIfUserExists(final String accessToken) throws UserNotFoundException, AuthenticationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if(userAuthTokenEntity == null)
            throw new UserNotFoundException("ATHR-001", "User has not signed in");
        UserEntity userEntity = userAuthTokenEntity.getUser();
        if(userEntity == null)
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        if(userAuthTokenEntity.getLogoutAt()!=null)
            throw new AuthenticationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");


        return userEntity;
    }
}

