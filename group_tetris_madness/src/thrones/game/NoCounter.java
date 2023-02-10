package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NoCounter implements ShortlistStrats{
    private List<Card> shortList = new ArrayList<>();
    private Hand playerHand;
    private Hand [] piles;
    private List<Card> cardDump;

    public void cardDump (Hand [] piles) {
        for(Hand pile : piles){
            for(Card card : pile.getCardList()){
                if(!cardDump.contains(card)) {
                    cardDump.add(card);
                }
            }
        }
    }

    public void setPlayerHand(Hand playerHand) {
        this.playerHand = playerHand;
    }

    public void setPiles(Hand[] piles) {
        this.piles = piles;
    }

    public NoCounter(){
        cardDump = new ArrayList<>();
    }

    public List<Card> getShortList() {
        cardDump(piles);
        for(Card card : playerHand.getCardList()){
            int myValue = ((GoTCard.Rank) card.getRank()).getRankValue();
            GoTCard.Suit mySuit = (GoTCard.Suit) card.getSuit();
            for(Card cardDumped : cardDump){
                int dumpedValue = ((GoTCard.Rank) cardDumped.getRank()).getRankValue();
                GoTCard.Suit dumpedSuit = (GoTCard.Suit) cardDumped.getSuit();

                if(myValue == dumpedValue && dumpedSuit.isMagic()){
                    shortList.add(card);
                }
            }

            for(Card myOtherCard: playerHand.getCardList()){
                int myOtherCardValue = ((GoTCard.Rank) myOtherCard.getRank()).getRankValue();
                GoTCard.Suit myOtherCardSuit = (GoTCard.Suit) myOtherCard.getSuit();
                if(!myOtherCard.equals(card)){
                    if(myOtherCardValue == myValue && myOtherCardSuit.isMagic()){
                        shortList.add(card);
                    }
                }
            }
            if(mySuit.isMagic()){
                shortList.add(card);
            }
        }

        return shortList;
    }
}
