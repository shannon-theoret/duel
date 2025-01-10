package com.shannontheoret.duel.dao;

import com.shannontheoret.duel.entity.Military;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MilitaryDao {

    private EntityManager entityManager;

    @Autowired
    public MilitaryDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void save(Military military) {
        Session currentSession = entityManager.unwrap(Session.class);
        currentSession.merge(military);
    }
}
