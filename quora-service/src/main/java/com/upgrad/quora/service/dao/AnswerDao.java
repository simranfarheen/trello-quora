package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity addAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public AnswerEntity editAnswer(final AnswerEntity answerEntity) {
        entityManager.merge(answerEntity);
        return answerEntity;
    }

    public AnswerEntity deleteQuestion(final AnswerEntity answer) {
        entityManager.remove(answer);
        return answer;
    }

    public AnswerEntity getAnswerByQuestionId(final String answerId) {
        try {
            return entityManager.createNamedQuery("answersByUuid", AnswerEntity.class).setParameter("uuid", answerId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<AnswerEntity> getAllAnswersByQuestionId(final String answerId) {
        try {
            return entityManager.createNamedQuery("answersByUuid", AnswerEntity.class).setParameter("uuid", answerId).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
