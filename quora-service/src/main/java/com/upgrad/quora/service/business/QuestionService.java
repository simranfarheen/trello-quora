package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
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


    public boolean checkIfQuestionExists(final String id){
        QuestionEntity questionEntity = questionDao.getQuestionById(id);
        if(questionEntity !=null)
            return true;
        return false;
    }
}
