package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerService {

    @Autowired
    AnswerDao answerDao;

    @Autowired
    QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(AnswerEntity answerEntity)  {
        return answerDao.addAnswer(answerEntity);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(UserEntity userEntity, String uuid, String editedAnswer) throws AuthorizationFailedException, AnswerNotFoundException {

        AnswerEntity answerEntity = answerDao.getAnswerByQuestionId(uuid);
        if(answerEntity==null)
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");

        if(!answerEntity.getUuid().equalsIgnoreCase(userEntity.getUuid()))
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");

        answerEntity.setAnswer(editedAnswer);
        answerDao.editAnswer(answerEntity);

        return answerEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswer(String uuid, UserEntity userEntity, UserAuthTokenEntity userAuthTokenEntity) throws AnswerNotFoundException, AuthorizationFailedException {

        AnswerEntity answerEntity = answerDao.getAnswerByQuestionId(uuid);
        if(answerEntity == null)
            throw new AnswerNotFoundException("QUES-001","Entered question uuid does not exist");

        if(answerEntity.getUuid() != userEntity.getUuid() || userAuthTokenEntity.getUuid()!=userEntity.getUuid())
            throw new AuthorizationFailedException("ATHR-003","Only the question owner can edit the question");

        answerDao.deleteQuestion(answerEntity);

        return answerEntity;
    }


    public List<AnswerEntity> getAllAnswers(String uuid) throws InvalidQuestionException {
        QuestionEntity questionEntity = questionDao.getQuestionById(uuid);
        if(questionEntity==null)
            throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");


        return answerDao.getAllAnswersByQuestionId(uuid);
    }
}
