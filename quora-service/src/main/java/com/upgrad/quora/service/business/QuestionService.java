package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity)  {
        return questionDao.createQuestion(questionEntity);
    }

    public List<QuestionEntity> getAllQuestions(){

        return questionDao.getAllQuestions();
    }

    public List<QuestionEntity> getQuestionsByUser(String userId){
        return questionDao.getQuestionByUserId(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(String uuid) throws InvalidQuestionException {

        QuestionEntity questionEntity = questionDao.getQuestionById(uuid);
        if(questionEntity == null)
            throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");

         questionDao.deleteQuestion(questionEntity);

         return questionEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestion(UserEntity userEntity, String uuid, String content) throws AuthorizationFailedException, InvalidQuestionException {

        QuestionEntity questionEntity = questionDao.getQuestionById(uuid);
        if(questionEntity==null)
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");

        if(!questionEntity.getUuid().equalsIgnoreCase(userEntity.getUuid()))
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");

        questionEntity.setContent(content);
        questionDao.updateQuestion(questionEntity);

        return questionEntity;
    }


    public boolean checkIfQuestionExists(final String id){
        QuestionEntity questionEntity = questionDao.getQuestionById(id);
        if(questionEntity !=null)
            return true;
        return false;
    }
}
