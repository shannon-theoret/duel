package com.shannontheoret.duel.card;

import java.util.EnumSet;
import java.util.Set;

public enum CardOrValueType {
    RAW_MATERIAL,
    MANUFACTURED_GOOD,
    CIVILIAN_BUILDING,
    SCIENTIFIC_BUILDING,
    COMMERCIAL_BUILDING,
    MILITARY_BUILDING,
    GUILD,
    WONDER,
    MONEY,
    RAW_MATERIAL_AND_MANUFACTURED_GOOD;

    public static Set<CardOrValueType> getCardTypes() {
        return EnumSet.range(RAW_MATERIAL, GUILD);
    }
}
