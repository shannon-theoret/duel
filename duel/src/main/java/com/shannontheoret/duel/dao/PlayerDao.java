package com.shannontheoret.duel.dao;

import com.shannontheoret.duel.entity.Game;
import com.shannontheoret.duel.entity.Player;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerDao {

    private EntityManager entityManager;

    @Autowired
    public PlayerDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void save(Player player) {
        Session currentSession = entityManager.unwrap(Session.class);
        currentSession.merge(player);
    }
}
