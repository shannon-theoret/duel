package com.shannontheoret.duel.entity;

import jakarta.persistence.*;

@Entity(name="military")
public class Military {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer militaryPosition = 0;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean loot5Player1Available = true;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean loot2Player1Available = true;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean loot2Player2Available = true;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean loot5Player2Available = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMilitaryPosition() {
        return militaryPosition;
    }

    public void setMilitaryPosition(Integer militaryPosition) {
        this.militaryPosition = militaryPosition;
    }

    public Boolean getLoot5Player1Available() {
        return loot5Player1Available;
    }

    public void setLoot5Player1Available(Boolean loot5Player1Available) {
        this.loot5Player1Available = loot5Player1Available;
    }

    public Boolean getLoot2Player1Available() {
        return loot2Player1Available;
    }

    public void setLoot2Player1Available(Boolean loot2Player1Available) {
        this.loot2Player1Available = loot2Player1Available;
    }

    public Boolean getLoot2Player2Available() {
        return loot2Player2Available;
    }

    public void setLoot2Player2Available(Boolean loot2Player2Available) {
        this.loot2Player2Available = loot2Player2Available;
    }

    public Boolean getLoot5Player2Available() {
        return loot5Player2Available;
    }

    public void setLoot5Player2Available(Boolean loot5Player2Available) {
        this.loot5Player2Available = loot5Player2Available;
    }
}
