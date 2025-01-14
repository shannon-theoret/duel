package com.shannontheoret.duel.card;

import java.util.*;

public enum CardName {
    AGE_ONE_BACK(null),
    AGE_TWO_BACK(null),
    AGE_THREE_BACK(null),
    GUILD_BACK(null),
    ALTER(new CivilianBuildingCard(1, new Cost(), 3)),
    APOTHECARY(new ScientificBuildingCard(1, new Cost(new ArrayList<>(Arrays.asList(Resource.GLASS))), 1, ScienceSymbol.WHEEL)),
    BATHS(new CivilianBuildingCard(1, new Cost(new ArrayList<>(Arrays.asList(Resource.STONE))), 3)),
    CLAY_PIT(new RawMaterialCard(1, new Cost(1), new ArrayList<>(Arrays.asList(Resource.BRICK)))),
    CLAY_POOL(new RawMaterialCard(1, new Cost(), new ArrayList<>(Arrays.asList(Resource.BRICK)))),
    CLAY_RESERVE(new CommercialBuildingCard(1, new Cost(), new HashSet<>(Set.of(Resource.BRICK)))),
    GARRISON(new MilitaryBuildingCard(1, new Cost(new ArrayList<>(Arrays.asList(Resource.BRICK))), 0,1)),
    GLASSWORKS(new ManufacturedGoodCard(1, new Cost(1), new ArrayList<>(Arrays.asList(Resource.GLASS)))),
    GUARD_TOWER(new MilitaryBuildingCard(1, new Cost(), 0,1)),
    LOGGING_CAMP(new RawMaterialCard(1, new Cost(1), new ArrayList<>(Arrays.asList(Resource.WOOD)))),
    LUMBER_YARD(new RawMaterialCard(1, new Cost(), new ArrayList<>(Arrays.asList(Resource.WOOD)))),
    PALISADE(new MilitaryBuildingCard(1, new Cost(2), 0, 1)),
    PHARMACIST(new ScientificBuildingCard(1, new Cost(2), 0,ScienceSymbol.MORTAR)),
    PRESS(new ManufacturedGoodCard(1, new Cost(1), new ArrayList<>(Arrays.asList(Resource.PAPYRUS)))),
    QUARRY(new RawMaterialCard(1, new Cost(), new ArrayList<>(Arrays.asList(Resource.STONE)))),
    SCRIPTORIUM(new ScientificBuildingCard(1, new Cost(2), 0, ScienceSymbol.QUILL)),
    STABLE(new MilitaryBuildingCard(1, new Cost(new ArrayList<>(Arrays.asList(Resource.WOOD))), 0, 1)),
    STONE_PIT(new RawMaterialCard(1, new Cost(1), new ArrayList<>(Arrays.asList(Resource.STONE)))),
    STONE_RESERVE(new CommercialBuildingCard(1, new Cost(3), new HashSet<>(Set.of(Resource.STONE)))),
    TAVERN(new CommercialBuildingCard(1, new Cost(),0, 4, null)),
    THEATRE(new CivilianBuildingCard(1, new Cost(), 3)),
    WOOD_RESERVE(new CommercialBuildingCard(1, new Cost(3), new HashSet<>(Set.of(Resource.WOOD)))),
    WORKSHOP(new ScientificBuildingCard(1, new Cost(new ArrayList<>(Arrays.asList(Resource.PAPYRUS))), 1, ScienceSymbol.PENDULUM)),
        AQUEDUCT(new CivilianBuildingCard(2,new Cost(0, new ArrayList<>(Arrays.asList(Resource.STONE, Resource.STONE, Resource.STONE)), CardName.BATHS), 5)),
        ARCHERY_RANGE(new MilitaryBuildingCard(2, new Cost(new ArrayList<>(Arrays.asList(Resource.STONE, Resource.WOOD, Resource.PAPYRUS))),0,2)),
        BARRACKS(new MilitaryBuildingCard(2, new Cost(3, new ArrayList<>(), CardName.GARRISON), 0, 1)),
        BREWERY(new CommercialBuildingCard(2, new Cost(),0, 6, null)),
        BRICKYARD(new RawMaterialCard(2, new Cost(2), new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.BRICK)))),
        CARAVANSERY(new CommercialBuildingCard(2, new Cost(2, new ArrayList<>(Arrays.asList(Resource.GLASS, Resource.PAPYRUS))), new ArrayList<>(Arrays.asList(Resource.WOOD, Resource.BRICK, Resource.STONE)))),
        COURTHOUSE(new CivilianBuildingCard(2, new Cost(new ArrayList<>(Arrays.asList(Resource.WOOD, Resource.WOOD, Resource.GLASS))), 5)),
        CUSTOMS_HOUSE(new CommercialBuildingCard(2, new Cost(4), new HashSet<>(Set.of(Resource.PAPYRUS, Resource.GLASS)))),
        DISPENSARY(new ScientificBuildingCard(2, new Cost(0, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.BRICK, Resource.STONE)), CardName.PHARMACIST), 2, ScienceSymbol.MORTAR)),
        DRYING_ROOM(new ManufacturedGoodCard(2, new Cost(), new ArrayList<>(Arrays.asList(Resource.PAPYRUS)))),
        FORUM(new CommercialBuildingCard(2, new Cost(3, new ArrayList<>(Arrays.asList(Resource.BRICK))), new ArrayList<>(Arrays.asList(Resource.GLASS, Resource.PAPYRUS)))),
        GLASSBLOWER(new ManufacturedGoodCard(2, new Cost(), new ArrayList<>(Arrays.asList(Resource.GLASS)))),
        HORSE_BREEDERS(new MilitaryBuildingCard(2, new Cost(0, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.WOOD)), CardName.STABLE), 0, 1)),
        LABORATORY(new ScientificBuildingCard(2, new Cost(new ArrayList<>(Arrays.asList(Resource.WOOD, Resource.GLASS, Resource.GLASS))), 1, ScienceSymbol.PENDULUM)),
        LIBRARY(new ScientificBuildingCard(2, new Cost(0, new ArrayList<>(Arrays.asList(Resource.STONE, Resource.WOOD, Resource.GLASS)), CardName.SCRIPTORIUM), 2, ScienceSymbol.QUILL)),
        PARADE_GROUND(new MilitaryBuildingCard(2, new Cost(new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.BRICK, Resource.GLASS))), 0, 2)),
        POSTRUM(new CivilianBuildingCard(2, new Cost(new ArrayList<>(Arrays.asList(Resource.STONE, Resource.WOOD))), 4)),
        SAWMILL(new RawMaterialCard(2, new Cost(2), new ArrayList<>(Arrays.asList(Resource.WOOD, Resource.WOOD)))),
        SCHOOL(new ScientificBuildingCard(2, new Cost(new ArrayList<>(Arrays.asList(Resource.WOOD, Resource.PAPYRUS, Resource.PAPYRUS))), 1, ScienceSymbol.WHEEL)),
        SHELF_QUARRY(new RawMaterialCard(2, new Cost(2), new ArrayList<>(Arrays.asList(Resource.STONE, Resource.STONE)))),
        STATUE(new CivilianBuildingCard(2, new Cost(0, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.BRICK)), CardName.THEATRE), 4)),
        TEMPLE(new CivilianBuildingCard(2, new Cost(0, new ArrayList<>(Arrays.asList(Resource.WOOD, Resource.PAPYRUS)), CardName.ALTER), 4)),
        WALLS(new MilitaryBuildingCard(2, new Cost(new ArrayList<>(Arrays.asList(Resource.STONE, Resource.STONE))), 0, 2)),
        ACADEMY(new ScientificBuildingCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.STONE, Resource.WOOD, Resource.GLASS, Resource.GLASS))), 3, ScienceSymbol.SUNDIAL)),
    ARENA(new CommercialBuildingCard(3, new Cost(0, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.STONE, Resource.WOOD)), CardName.BREWERY), 3, 2, CardOrValueType.WONDER)),
    ARMORY(new CommercialBuildingCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.STONE, Resource.STONE, Resource.GLASS))), 3, 1, CardOrValueType.MILITARY_BUILDING)),
    ARSENAL(new MilitaryBuildingCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.BRICK, Resource.BRICK, Resource.WOOD, Resource.WOOD))), 0, 3)),
    CHAMBER_OF_COMMERCE(new CommercialBuildingCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.PAPYRUS, Resource.PAPYRUS))), 3, 3, CardOrValueType.MANUFACTURED_GOOD)),
    CIRCUS(new MilitaryBuildingCard(3, new Cost(0, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.BRICK, Resource.STONE, Resource.STONE)), CardName.PARADE_GROUND), 0, 2)),
    FORTIFICATIONS(new MilitaryBuildingCard(3, new Cost(0, new ArrayList<>(Arrays.asList(Resource.STONE, Resource.STONE, Resource.BRICK, Resource.PAPYRUS)), CardName.PALISADE), 0, 2)),
    GARDENS(new CivilianBuildingCard(3, new Cost(0, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.BRICK, Resource.WOOD, Resource.WOOD)), CardName.STATUE), 6)),
    LIGHTHOUSE(new CommercialBuildingCard(3, new Cost(0, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.BRICK, Resource.GLASS)), CardName.TAVERN), 3,1, CardOrValueType.COMMERCIAL_BUILDING)),
    OBELISK(new CivilianBuildingCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.STONE, Resource.STONE, Resource.GLASS))), 5)),
    OBSERVATORY(new ScientificBuildingCard(3, new Cost(0, new ArrayList<>(Arrays.asList(Resource.STONE, Resource.PAPYRUS, Resource.PAPYRUS)), CardName.LABORATORY),2, ScienceSymbol.GLOBE)),
    PALACE(new CivilianBuildingCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.STONE, Resource.WOOD, Resource.GLASS, Resource.GLASS))), 7)),
    PANTHEON(new CivilianBuildingCard(3, new Cost(0, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.WOOD, Resource.PAPYRUS, Resource.PAPYRUS)), CardName.TEMPLE), 6)),
    PORT(new CommercialBuildingCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.WOOD, Resource.GLASS, Resource.PAPYRUS))), 3, 2, CardOrValueType.RAW_MATERIAL)),
    PRETORIUM(new MilitaryBuildingCard(3, new Cost(8), 0, 3)),
    SENATE(new CivilianBuildingCard(3, new Cost(0, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.BRICK, Resource.STONE, Resource.PAPYRUS)), CardName.POSTRUM), 5)),
    SIEGE_WORKSHOP(new MilitaryBuildingCard(3, new Cost(0, new ArrayList<>(Arrays.asList(Resource.WOOD,Resource.WOOD,Resource.WOOD, Resource.GLASS)), CardName.ARCHERY_RANGE), 0, 2)),
    STUDY(new ScientificBuildingCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.WOOD, Resource.WOOD, Resource.GLASS, Resource.PAPYRUS))), 3, ScienceSymbol.SUNDIAL)),
    TOWNHALL(new CivilianBuildingCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.STONE, Resource.STONE, Resource.STONE, Resource.WOOD, Resource.WOOD))), 7)),
    UNIVERSITY(new ScientificBuildingCard(3, new Cost(0, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.GLASS, Resource.PAPYRUS)), CardName.SCHOOL), 2, ScienceSymbol.GLOBE)),
    BUILDERS_GUILD(new GuildCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.STONE, Resource.STONE, Resource.BRICK, Resource.WOOD, Resource.GLASS))), CardOrValueType.WONDER, 1, 2, 0)),
    MAGISTRATES_GUILD(new GuildCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.WOOD, Resource.WOOD, Resource.BRICK, Resource.PAPYRUS))), CardOrValueType.CIVILIAN_BUILDING, 1, 1, 1)),
    MERCHANTS_GUILD(new GuildCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.WOOD, Resource.GLASS, Resource.PAPYRUS))), CardOrValueType.COMMERCIAL_BUILDING, 1, 1,1 )),
    MONEYLENDERS_GUILD(new GuildCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.STONE, Resource.STONE, Resource.WOOD, Resource.WOOD))), CardOrValueType.MONEY, 3, 1, 0)),
    SCIENTISTS_GUILD(new GuildCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.BRICK, Resource.WOOD, Resource.WOOD))), CardOrValueType.SCIENTIFIC_BUILDING, 1, 1, 1)),
    SHIPOWNERS_GUILD(new GuildCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.STONE, Resource.GLASS, Resource.PAPYRUS))), CardOrValueType.RAW_MATERIAL_AND_MANUFACTURED_GOOD, 1, 1, 1)),
    TACTICIANS_GUILD(new GuildCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.STONE, Resource.STONE, Resource.BRICK, Resource.PAPYRUS))), CardOrValueType.MILITARY_BUILDING, 1, 1, 1));

    private final Card card;

    CardName(Card card) {
        this.card = card;
    }

    public Card getCard() {
        return this.card;
    }

    public static Set<CardName> getAgeOne() {
        return EnumSet.range(ALTER, WORKSHOP);
    }

    public static Set<CardName> getAgeTwo() { return EnumSet.range(AQUEDUCT, WALLS); }

    public static Set<CardName> getAgeThreeNonGuild() { return EnumSet.range(ACADEMY, UNIVERSITY);}

    public static Set<CardName> getGuilds() { return EnumSet.range(BUILDERS_GUILD, TACTICIANS_GUILD);}
    public static CardName getBack(Integer age) {
        switch (age) {
            case 1:
                return AGE_ONE_BACK;
            case 2:
                return AGE_TWO_BACK;
            default:
                return AGE_THREE_BACK;
        }
    }
}
