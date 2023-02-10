package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class SmartStrategy extends PlayerStrategy{

    private NoCounter noCounter = new NoCounter();
    private InFavour inFavour = new InFavour();

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

    public void updateSmart(Hand[] piles){
        noCounter.cardDump(piles);
    }

    public SmartStrategy(){};

    public Optional<Card> waitForValidCard() {
        Random random = new Random();
        List<Card> shortListCards = new ArrayList<>();
        if(heartTurn(piles)){
            for(Card card: playerHand.getCardList()){
                GoTCard.Suit suit = (GoTCard.Suit) card.getSuit();
                if(suit.isCharacter()){
                    shortListCards.add(card);
                }
            }
        }else{
            Hand[] copy = piles.clone();
            inFavour.setPlayerHand(playerHand);
            inFavour.setPiles(copy);
            inFavour.setPlayerIndex(playerIndex);
            List<Card> inFavourList = inFavour.getShortList();

            noCounter.setPlayerHand(playerHand);
            noCounter.setPiles(copy);
            List<Card> noCounterList = noCounter.getShortList();

            for(Card inFavourCard: inFavourList){
                if(noCounterList.contains(inFavourCard)){
                    shortListCards.add(inFavourCard);
                }
            }

        }
        if (shortListCards.isEmpty()) {
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
