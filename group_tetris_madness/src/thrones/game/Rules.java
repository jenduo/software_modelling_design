package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class Rules {

     public static void validate(Card card, int selectedPileIndex, Hand[] piles) throws BrokeRuleException{
         GoTCard.Suit suit = (GoTCard.Suit) card.getSuit();
         try {
             GoTCard.Suit topPile0 = (GoTCard.Suit) piles[0].getLast().getSuit();
             GoTCard.Suit topPile1 = (GoTCard.Suit) piles[1].getLast().getSuit();
             GoTCard.Suit topPileSuits[] = {topPile0, topPile1};
             if (suit.isMagic() && topPileSuits[selectedPileIndex].isCharacter()) {
                 throw new BrokeRuleException("Can not place Diamond here");
             } else if (suit.isCharacter() && topPileSuits[selectedPileIndex] != null) {
                 throw new BrokeRuleException("Can not play Heart Card");
             }
         }
         catch (NullPointerException ex){
             if(!suit.isCharacter()){
                 throw new BrokeRuleException("Must play Heart Card");
             }
         }

    }
}
