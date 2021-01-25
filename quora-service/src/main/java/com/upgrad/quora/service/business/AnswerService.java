package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.entity.AnswerEntity;
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

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(AnswerEntity answerEntity)  {
        return answerDao.addAnswer(answerEntity);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(UserEntity userEntity, String uuid, String editedAnswer) throws AuthorizationFailedException, InvalidQuestionException {

        AnswerEntity answerEntity = answerDao.getAnswerByQuestionId(uuid);
        if(answerEntity==null)
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");

        if(!answerEntity.getUuid().equalsIgnoreCase(userEntity.getUuid()))
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");

        answerEntity.setAnswer(editedAnswer);
        answerDao.editAnswer(answerEntity);

        return answerEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswer(String uuid) throws AnswerNotFoundException {

        AnswerEntity answerEntity = answerDao.getAnswerByQuestionId(uuid);
        if(answerEntity == null)
            throw new AnswerNotFoundException("QUES-001","Entered question uuid does not exist");

        answerDao.deleteQuestion(answerEntity);

        return answerEntity;
    }


    public List<AnswerEntity> getAllAnswers(String uuid){
        return answerDao.getAllAnswersByQuestionId(uuid);
    }
}
