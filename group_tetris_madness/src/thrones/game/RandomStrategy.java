package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RandomStrategy extends PlayerStrategy{
    private Hand playerHand;
    private Hand [] piles;
    private Optional<Card> selected;
    private Card finSelected;

    public void setPlayerHand(Hand playerHand) {
        this.playerHand = playerHand;
    }

    public void setPiles(Hand[] piles) {
        this.piles = piles;
    }

    public void setFinSelected(Card finSelected) {
        this.finSelected = finSelected;
    }

    public RandomStrategy(){}



    @Override
    public Optional<Card> waitForValidCard() {
        Random random = new Random();
        List<Card> shortListCards = new ArrayList<>();
        for (Card card:playerHand.getCardList()) {
            for (int i =0; i<2;i++) {
                try
                {
                    Rules.validate(card,i,piles);
                    if (!shortListCards.contains(card)) {
                        shortListCards.add(card);
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
        List<Integer> shortListPiles = new ArrayList<>();
        Random random = new Random();
        for (int i =0; i<2;i++) {
            try
            {
                Rules.validate(finSelected,i,piles);
                shortListPiles.add(i);
            }
            catch(BrokeRuleException ex)
            {

            }
        }
        if(shortListPiles.size()==2){
            return random.nextInt(2);
        }else{
            return shortListPiles.get(0);
        }
    }
}
