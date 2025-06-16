package com.shannontheoret.duel.card;

import java.util.*;

public enum CardName {
    AGE_ONE_BACK(null, null),
    AGE_TWO_BACK(null, null),
    AGE_THREE_BACK(null, null),
    GUILD_BACK(null, null),
    ALTER("Altar", new CivilianBuildingCard(1, new Cost(), 3)),
    APOTHECARY("Apothecary", new ScientificBuildingCard(1, new Cost(new ArrayList<>(Arrays.asList(Resource.GLASS))), 1, ScienceSymbol.WHEEL)),
    BATHS("Baths", new CivilianBuildingCard(1, new Cost(new ArrayList<>(Arrays.asList(Resource.STONE))), 3)),
    CLAY_PIT("Clay Pit", new RawMaterialCard(1, new Cost(1), new ArrayList<>(Arrays.asList(Resource.BRICK)))),
    CLAY_POOL("Clay Pool", new RawMaterialCard(1, new Cost(), new ArrayList<>(Arrays.asList(Resource.BRICK)))),
    CLAY_RESERVE("Clay Reserve", new CommercialBuildingCard(1, new Cost(3), new HashSet<>(Set.of(Resource.BRICK)))),
    GARRISON("Garrison", new MilitaryBuildingCard(1, new Cost(new ArrayList<>(Arrays.asList(Resource.BRICK))), 0,1)),
    GLASSWORKS("Glassworks", new ManufacturedGoodCard(1, new Cost(1), new ArrayList<>(Arrays.asList(Resource.GLASS)))),
    GUARD_TOWER("Guard Tower", new MilitaryBuildingCard(1, new Cost(), 0,1)),
    LOGGING_CAMP("Logging Camp", new RawMaterialCard(1, new Cost(1), new ArrayList<>(Arrays.asList(Resource.WOOD)))),
    LUMBER_YARD("Lumber Yard", new RawMaterialCard(1, new Cost(), new ArrayList<>(Arrays.asList(Resource.WOOD)))),
    PALISADE("Palisade", new MilitaryBuildingCard(1, new Cost(2), 0, 1)),
    PHARMACIST("Pharmacist", new ScientificBuildingCard(1, new Cost(2), 0, ScienceSymbol.MORTAR)),
    PRESS("Press", new ManufacturedGoodCard(1, new Cost(1), new ArrayList<>(Arrays.asList(Resource.PAPYRUS)))),
    QUARRY("Quarry", new RawMaterialCard(1, new Cost(), new ArrayList<>(Arrays.asList(Resource.STONE)))),
    SCRIPTORIUM("Scriptorium", new ScientificBuildingCard(1, new Cost(2), 0, ScienceSymbol.QUILL)),
    STABLE("Stable", new MilitaryBuildingCard(1, new Cost(new ArrayList<>(Arrays.asList(Resource.WOOD))), 0, 1)),
    STONE_PIT("Stone Pit", new RawMaterialCard(1, new Cost(1), new ArrayList<>(Arrays.asList(Resource.STONE)))),
    STONE_RESERVE("Stone Reserve", new CommercialBuildingCard(1, new Cost(3), new HashSet<>(Set.of(Resource.STONE)))),
    TAVERN("Tavern", new CommercialBuildingCard(1, new Cost(), 0, 4, null)),
    THEATRE("Theatre", new CivilianBuildingCard(1, new Cost(), 3)),
    WOOD_RESERVE("Wood Reserve", new CommercialBuildingCard(1, new Cost(3), new HashSet<>(Set.of(Resource.WOOD)))),
    WORKSHOP("Workshop", new ScientificBuildingCard(1, new Cost(new ArrayList<>(Arrays.asList(Resource.PAPYRUS))), 1, ScienceSymbol.PENDULUM)),
    AQUEDUCT("Aqueduct", new CivilianBuildingCard(2,new Cost(0, new ArrayList<>(Arrays.asList(Resource.STONE, Resource.STONE, Resource.STONE)), CardName.BATHS), 5)),
    ARCHERY_RANGE("Archery Range", new MilitaryBuildingCard(2, new Cost(new ArrayList<>(Arrays.asList(Resource.STONE, Resource.WOOD, Resource.PAPYRUS))), 0, 2)),
    BARRACKS("Barracks", new MilitaryBuildingCard(2, new Cost(3, new ArrayList<>(), CardName.GARRISON), 0, 1)),
    BREWERY("Brewery", new CommercialBuildingCard(2, new Cost(), 0, 6, null)),
    BRICKYARD("Brickyard", new RawMaterialCard(2, new Cost(2), new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.BRICK)))),
    CARAVANSERY("Caravansery", new CommercialBuildingCard(2, new Cost(2, new ArrayList<>(Arrays.asList(Resource.GLASS, Resource.PAPYRUS))), new ArrayList<>(Arrays.asList(Resource.WOOD, Resource.BRICK, Resource.STONE)))),
    COURTHOUSE("Courthouse", new CivilianBuildingCard(2, new Cost(new ArrayList<>(Arrays.asList(Resource.WOOD, Resource.WOOD, Resource.GLASS))), 5)),
    CUSTOMS_HOUSE("Customs House", new CommercialBuildingCard(2, new Cost(4), new HashSet<>(Set.of(Resource.PAPYRUS, Resource.GLASS)))),
    DISPENSARY("Dispensary", new ScientificBuildingCard(2, new Cost(0, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.BRICK, Resource.STONE)), CardName.PHARMACIST), 2, ScienceSymbol.MORTAR)),
    DRYING_ROOM("Drying Room", new ManufacturedGoodCard(2, new Cost(), new ArrayList<>(Arrays.asList(Resource.PAPYRUS)))),
    FORUM("Forum", new CommercialBuildingCard(2, new Cost(3, new ArrayList<>(Arrays.asList(Resource.BRICK))), new ArrayList<>(Arrays.asList(Resource.GLASS, Resource.PAPYRUS)))),
    GLASSBLOWER("Glassblower", new ManufacturedGoodCard(2, new Cost(), new ArrayList<>(Arrays.asList(Resource.GLASS)))),
    HORSE_BREEDERS("Horse Breeders", new MilitaryBuildingCard(2, new Cost(0, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.WOOD)), CardName.STABLE), 0, 1)),
    LABORATORY("Laboratory", new ScientificBuildingCard(2, new Cost(new ArrayList<>(Arrays.asList(Resource.WOOD, Resource.GLASS, Resource.GLASS))), 1, ScienceSymbol.PENDULUM)),
    LIBRARY("Library", new ScientificBuildingCard(2, new Cost(0, new ArrayList<>(Arrays.asList(Resource.STONE, Resource.WOOD, Resource.GLASS)), CardName.SCRIPTORIUM), 2, ScienceSymbol.QUILL)),
    PARADE_GROUND("Parade Ground", new MilitaryBuildingCard(2, new Cost(new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.BRICK, Resource.GLASS))), 0, 2)),
    POSTRUM("Postrum", new CivilianBuildingCard(2, new Cost(new ArrayList<>(Arrays.asList(Resource.STONE, Resource.WOOD))), 4)),
    SAWMILL("Sawmill", new RawMaterialCard(2, new Cost(2), new ArrayList<>(Arrays.asList(Resource.WOOD, Resource.WOOD)))),
    SCHOOL("School", new ScientificBuildingCard(2, new Cost(new ArrayList<>(Arrays.asList(Resource.WOOD, Resource.PAPYRUS, Resource.PAPYRUS))), 1, ScienceSymbol.WHEEL)),
    SHELF_QUARRY("Shelf Quarry", new RawMaterialCard(2, new Cost(2), new ArrayList<>(Arrays.asList(Resource.STONE, Resource.STONE)))),
    STATUE("Statue", new CivilianBuildingCard(2, new Cost(0, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.BRICK)), CardName.THEATRE), 4)),
    TEMPLE("Temple", new CivilianBuildingCard(2, new Cost(0, new ArrayList<>(Arrays.asList(Resource.WOOD, Resource.PAPYRUS)), CardName.ALTER), 4)),
    WALLS("Walls", new MilitaryBuildingCard(2, new Cost(new ArrayList<>(Arrays.asList(Resource.STONE, Resource.STONE))), 0, 2)),
    ACADEMY("Academy", new ScientificBuildingCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.STONE, Resource.WOOD, Resource.GLASS, Resource.GLASS))), 3, ScienceSymbol.SUNDIAL)),
    ARENA("Arena", new CommercialBuildingCard(3, new Cost(0, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.STONE, Resource.WOOD)), CardName.BREWERY), 3, 2, CardOrValueType.WONDER)),
    ARMORY("Armory", new CommercialBuildingCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.STONE, Resource.STONE, Resource.GLASS))), 3, 1, CardOrValueType.MILITARY_BUILDING)),
    ARSENAL("Arsenal", new MilitaryBuildingCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.BRICK, Resource.BRICK, Resource.WOOD, Resource.WOOD))), 0, 3)),
    CHAMBER_OF_COMMERCE("Chamber of Commerce", new CommercialBuildingCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.PAPYRUS, Resource.PAPYRUS))), 3, 3, CardOrValueType.MANUFACTURED_GOOD)),
    CIRCUS("Circus", new MilitaryBuildingCard(3, new Cost(0, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.BRICK, Resource.STONE, Resource.STONE)), CardName.PARADE_GROUND), 0, 2)),
    FORTIFICATIONS("Fortifications", new MilitaryBuildingCard(3, new Cost(0, new ArrayList<>(Arrays.asList(Resource.STONE, Resource.STONE, Resource.BRICK, Resource.PAPYRUS)), CardName.PALISADE), 0, 2)),
    GARDENS("Gardens", new CivilianBuildingCard(3, new Cost(0, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.BRICK, Resource.WOOD, Resource.WOOD)), CardName.STATUE), 6)),
    LIGHTHOUSE("Lighthouse", new CommercialBuildingCard(3, new Cost(0, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.BRICK, Resource.GLASS)), CardName.TAVERN), 3, 1, CardOrValueType.COMMERCIAL_BUILDING)),
    OBELISK("Obelisk", new CivilianBuildingCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.STONE, Resource.STONE, Resource.GLASS))), 5)),
    OBSERVATORY("Observatory", new ScientificBuildingCard(3, new Cost(0, new ArrayList<>(Arrays.asList(Resource.STONE, Resource.PAPYRUS, Resource.PAPYRUS)), CardName.LABORATORY), 2, ScienceSymbol.GLOBE)),
    PALACE("Palace", new CivilianBuildingCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.STONE, Resource.WOOD, Resource.GLASS, Resource.GLASS))), 7)),
    PANTHEON("Pantheon", new CivilianBuildingCard(3, new Cost(0, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.WOOD, Resource.PAPYRUS, Resource.PAPYRUS)), CardName.TEMPLE), 6)),
    PORT("Port", new CommercialBuildingCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.WOOD, Resource.GLASS, Resource.PAPYRUS))), 3, 2, CardOrValueType.RAW_MATERIAL)),
    PRETORIUM("Pretorium", new MilitaryBuildingCard(3, new Cost(8), 0, 3)),
    SENATE("Senate", new CivilianBuildingCard(3, new Cost(0, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.BRICK, Resource.STONE, Resource.PAPYRUS)), CardName.POSTRUM), 5)),
    SIEGE_WORKSHOP("Siege Workshop", new MilitaryBuildingCard(3, new Cost(0, new ArrayList<>(Arrays.asList(Resource.WOOD, Resource.WOOD, Resource.WOOD, Resource.GLASS)), CardName.ARCHERY_RANGE), 0, 2)),
    STUDY("Study", new ScientificBuildingCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.WOOD, Resource.WOOD, Resource.GLASS, Resource.PAPYRUS))), 3, ScienceSymbol.SUNDIAL)),
    TOWNHALL("Townhall", new CivilianBuildingCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.STONE, Resource.STONE, Resource.STONE, Resource.WOOD, Resource.WOOD))), 7)),
    UNIVERSITY("University", new ScientificBuildingCard(3, new Cost(0, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.GLASS, Resource.PAPYRUS)), CardName.SCHOOL), 2, ScienceSymbol.GLOBE)),
    BUILDERS_GUILD("Builders Guild", new GuildCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.STONE, Resource.STONE, Resource.BRICK, Resource.WOOD, Resource.GLASS))), CardOrValueType.WONDER, 1, 2, 0)),
    MAGISTRATES_GUILD("Magistrates Guild", new GuildCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.WOOD, Resource.WOOD, Resource.BRICK, Resource.PAPYRUS))), CardOrValueType.CIVILIAN_BUILDING, 1, 1, 1)),
    MERCHANTS_GUILD("Merchants Guild", new GuildCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.WOOD, Resource.GLASS, Resource.PAPYRUS))), CardOrValueType.COMMERCIAL_BUILDING, 1, 1, 1)),
    MONEYLENDERS_GUILD("Moneylenders Guild", new GuildCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.STONE, Resource.STONE, Resource.WOOD, Resource.WOOD))), CardOrValueType.MONEY, 3, 1, 0)),
    SCIENTISTS_GUILD("Scientists Guild", new GuildCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.BRICK, Resource.WOOD, Resource.WOOD))), CardOrValueType.SCIENTIFIC_BUILDING, 1, 1, 1)),
    SHIPOWNERS_GUILD("Shipowners Guild", new GuildCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.STONE, Resource.GLASS, Resource.PAPYRUS))), CardOrValueType.RAW_MATERIAL_AND_MANUFACTURED_GOOD, 1, 1, 1)),
    TACTICIANS_GUILD("Tacticians Guild", new GuildCard(3, new Cost(new ArrayList<>(Arrays.asList(Resource.STONE, Resource.STONE, Resource.BRICK, Resource.PAPYRUS))), CardOrValueType.MILITARY_BUILDING, 1, 1, 1));

    private final String cardTitle;
    private final Card card;

    CardName(String cardTitle, Card card) {
        this.cardTitle = cardTitle;
        this.card = card;
    }

    public String getCardTitle() {return this.cardTitle;}

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
