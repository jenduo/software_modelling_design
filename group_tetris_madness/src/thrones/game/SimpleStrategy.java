package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class SimpleStrategy extends PlayerStrategy{
    private Hand playerHand;
    private Hand [] piles;
    private int playerIndex;
    private Optional<Card> selected;
    private Card finSelected;

    public void setPlayerHand(Hand playerHand) {
        this.playerHand = playerHand;
    }

    public void setPiles(Hand[] piles) {
        this.piles = piles;
    }

    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    public void setFinSelected(Card finSelected) {
        this.finSelected = finSelected;
    }

    public SimpleStrategy(){}

    @Override
    public Optional<Card> waitForValidCard() {
        Random random = new Random();
        List<Card> shortListCards = new ArrayList<>();
        for (Card card: playerHand.getCardList()) {
            for (int i =0; i<2;i++) {
                try
                {
                    Rules.validate(card,i,piles);
                    if (!shortListCards.contains(card)) {
                        GoTCard.Suit suit = (GoTCard.Suit) card.getSuit();
                        if (suit.isMagic()){
                            if(playerIndex == 0 || playerIndex == 2){
                                if(i == 1){
                                    shortListCards.add(card);
                                }
                            }
                            if(playerIndex == 1 || playerIndex == 3){
                                if(i == 0){
                                    shortListCards.add(card);
                                }
                            }
                        }else{
                            shortListCards.add(card);
                        }
                    }
                }
                catch(BrokeRuleException ex)
                {

                }
            }

        }
        if (shortListCards.isEmpty()||(random.nextInt(3) ==0 && !heartTurn(piles))) {
            selected = Optional.empty();
        } else {
            selected = Optional.of(shortListCards.get(random.nextInt(shortListCards.size())));
        }
        return selected;
    }

    @Override
    public int waitForValidPile() {
        GoTCard.Suit suit = (GoTCard.Suit) finSelected.getSuit();
        if(playerIndex == 0 || playerIndex == 2){
            if(suit.isMagic()){
                return 1;
            }else{
                return 0;
            }
        } else {
            if(suit.isMagic()){
                return 0;
            }else{
                return 1;
            }
        }
    }
}
