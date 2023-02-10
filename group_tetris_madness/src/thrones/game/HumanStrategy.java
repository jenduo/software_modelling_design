package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.CardAdapter;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class HumanStrategy extends PlayerStrategy{
    private Hand playerHand;
    private Hand [] piles;
    private Optional<Card> selected;
    private Card finSelected;
    private int selectedPileIndex;
    private int NON_SELECTION_VALUE = -1;
    private DisplayManager dm;

    public void setPlayerHand(Hand playerHand) {
        this.playerHand = playerHand;
    }
    public void setPiles(Hand[] piles) {
        this.piles = piles;
    }
    public void setFinSelected(Card finSelected){this.finSelected = finSelected;}
    public void setDm(DisplayManager dm){this.dm = dm;}

    public void setupListener(){
        playerHand.addCardListener(new CardAdapter() {
            public void leftDoubleClicked(Card card) {
                selected = Optional.of(card);
                playerHand.setTouchEnabled(false);
            }
            public void rightClicked(Card card) {
                selected = Optional.empty(); // Don't care which card we right-clicked for player to pass
                playerHand.setTouchEnabled(false);
            }
        });
    }

    public void setupPileListener() {
        for (int i = 0; i < 2; i++) {
            final Hand currentPile = piles[i];
            final int pileIndex = i;
            piles[i].addCardListener(new CardAdapter() {
                public void leftClicked(Card card) {
                    selectedPileIndex = pileIndex;
                    currentPile.setTouchEnabled(false);
                }
            });
        }
    }

    @Override
    public Optional<Card> waitForValidCard() {
        setupListener();
        if (playerHand.isEmpty()) {
            selected = Optional.empty();
        } else {
            selected = null;
            playerHand.setTouchEnabled(true);
            do {
                if (selected == null) {
                    dm.delay(100);
                    continue;
                }

                List<Card> shortListCards = new ArrayList<>();
                for (Card card:playerHand.getCardList()) {
                    for (int i = 0; i < 2; i++) {
                        try {
                            Rules.validate(card, i, piles);
                            if (!shortListCards.contains(card)) {
                                shortListCards.add(card);
                            }
                        } catch (BrokeRuleException ex) {

                        }
                    }
                }
                if(heartTurn(piles) && !selected.isEmpty() && shortListCards.contains(selected.get())) {
                    break;
                }else if (!heartTurn(piles) && (selected.isEmpty()||shortListCards.contains(selected.get()))){
                    break;
                } else {
                    selected = null;
                    playerHand.setTouchEnabled(true);
                }
                dm.delay(100);
            } while (true);
        }
        return selected;
    }


    @Override
    public int waitForValidPile() {
        setupPileListener();
        int notAllowed = NON_SELECTION_VALUE;
        for (int i =0; i<2;i++) {
            try
            {
                Rules.validate(finSelected,i,piles);
            }
            catch(BrokeRuleException ex)
            {
                notAllowed = i;
            }
        }

        selectedPileIndex = NON_SELECTION_VALUE;
        for (Hand pile : piles) {
            pile.setTouchEnabled(true);
        }
        while(selectedPileIndex == NON_SELECTION_VALUE || selectedPileIndex == notAllowed) {
            dm.delay(100);
        }
        for (Hand pile : piles) {
            pile.setTouchEnabled(false);
        }
        return this.selectedPileIndex;
    }
}
