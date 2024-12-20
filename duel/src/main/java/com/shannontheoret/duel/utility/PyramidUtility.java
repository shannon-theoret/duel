package com.shannontheoret.duel.utility;

import com.shannontheoret.duel.CardDTO;
import com.shannontheoret.duel.card.CardName;
import com.shannontheoret.duel.card.CardOrValueType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PyramidUtility {

    public static List<PyramidProperty> ageOne = new ArrayList<>(20);
    public static List<PyramidProperty> ageTwo = new ArrayList<>(20);
    public static List<PyramidProperty> ageThree = new ArrayList<>(20);


    static {
        ageOne.add(0, new PyramidProperty(true, 2 ,3));
        ageOne.add(1, new PyramidProperty(true, 3, 4));
        ageOne.add(2, new PyramidProperty(false, 5, 6));
        ageOne.add(3, new PyramidProperty(false, 6, 7));
        ageOne.add(4, new PyramidProperty(false, 7, 8));
        ageOne.add(5, new PyramidProperty(true, 9, 10));
        ageOne.add(6, new PyramidProperty(true, 10, 11));
        ageOne.add(7, new PyramidProperty(true, 11 , 12));
        ageOne.add(8, new PyramidProperty(true, 12 ,13));
        ageOne.add(9, new PyramidProperty(false, 14,15));
        ageOne.add(10, new PyramidProperty(false, 15, 16));
        ageOne.add(11, new PyramidProperty(false, 16, 17));
        ageOne.add(12, new PyramidProperty(false, 17,18));
        ageOne.add(13, new PyramidProperty(false, 18, 19));
        ageOne.add(14, new PyramidProperty());
        ageOne.add(15, new PyramidProperty());
        ageOne.add(16, new PyramidProperty());
        ageOne.add(17, new PyramidProperty());
        ageOne.add(18, new PyramidProperty());
        ageOne.add(19, new PyramidProperty());

        ageTwo.add(0, new PyramidProperty(true, -1, 6));
        ageTwo.add(1, new PyramidProperty(true, 6, 7));
        ageTwo.add(2, new PyramidProperty(true, 7, 8));
        ageTwo.add(3, new PyramidProperty(true, 8, 9));
        ageTwo.add(4, new PyramidProperty(true, 9, 10));
        ageTwo.add(5, new PyramidProperty(true, 10, -1));
        ageTwo.add(6, new PyramidProperty(false, -1, 11));
        ageTwo.add(7, new PyramidProperty(false, 11, 12));
        ageTwo.add(8, new PyramidProperty(false, 12, 13));
        ageTwo.add(9, new PyramidProperty(false, 13,14));
        ageTwo.add(10, new PyramidProperty(false, 14, -1));
        ageTwo.add(11, new PyramidProperty(true, -1, 15));
        ageTwo.add(12, new PyramidProperty(true, 15, 16));
        ageTwo.add(13, new PyramidProperty(true, 16, 17));
        ageTwo.add(14, new PyramidProperty(true, 17, -1));
        ageTwo.add(15, new PyramidProperty(false, -1, 18));
        ageTwo.add(16, new PyramidProperty(false, 18, 19));
        ageTwo.add(17, new PyramidProperty(false, 19, -1));
        ageTwo.add(18, new PyramidProperty());
        ageTwo.add(19, new PyramidProperty());

        ageThree.add(0, new PyramidProperty(true, 2, 3));
        ageThree.add(1, new PyramidProperty(true, 3,4));
        ageThree.add(2, new PyramidProperty(false, 5,6));
        ageThree.add(3, new PyramidProperty(false, 6,7));
        ageThree.add(4, new PyramidProperty(false, 7, 6));
        ageThree.add(5, new PyramidProperty(true, -1, 9));
        ageThree.add(6, new PyramidProperty(true, 9, -1));
        ageThree.add(7, new PyramidProperty(true, -1, 10));
        ageThree.add(8, new PyramidProperty(true, 10, -1));
        ageThree.add(9, new PyramidProperty(false, 11, 12));
        ageThree.add(10, new PyramidProperty(false, 13, 14));
        ageThree.add(11, new PyramidProperty(true, -1, 15));
        ageThree.add(12, new PyramidProperty(true, 15,16));
        ageThree.add(13, new PyramidProperty(true, 16, 17));
        ageThree.add(14, new PyramidProperty(true, 17, -1));
        ageThree.add(15, new PyramidProperty(false, -1, 18));
        ageThree.add(16, new PyramidProperty(false, 18, 19));
        ageThree.add(17, new PyramidProperty(false, 19, -1));
        ageThree.add(18, new PyramidProperty());
        ageThree.add(19, new PyramidProperty());
    }


    public static Map<Integer, CardDTO> generateVisiblePyramid(Integer age, Map<Integer, CardName> pyramid) {
        Map<Integer, CardDTO> visiblePyramid = new HashMap<>();

        List<PyramidProperty> pyramidProperties;
        switch (age) {
            case 1:
                pyramidProperties = ageOne;
                break;
            case 2:
                pyramidProperties = ageTwo;
                break;
            case 3:
                pyramidProperties = ageThree;
                break;
            default:
                pyramidProperties = ageOne;
                break;
        }
        for (Map.Entry<Integer, CardName> card : pyramid.entrySet()) {
            PyramidProperty pyramidProperty = pyramidProperties.get(card.getKey());
            boolean isActive = !pyramid.containsKey(pyramidProperty.getLeftIndexEmptyToBeActive())
                    && !pyramid.containsKey(pyramidProperty.getRightIndexEmptyToBeActive());
            boolean isVisible = (pyramidProperty.alwaysVisible || isActive);
            CardName cardName;
            if (isVisible) {
                cardName = card.getValue();
            } else if (card.getValue().getCard().getCardType() == CardOrValueType.GUILD) {
                cardName = CardName.GUILD_BACK;
            } else {
                cardName = CardName.getBack(age);
            }
            CardDTO cardDTO = new CardDTO(cardName, isActive);
            visiblePyramid.put(card.getKey(), cardDTO);
        }
        return visiblePyramid;
    }
}
