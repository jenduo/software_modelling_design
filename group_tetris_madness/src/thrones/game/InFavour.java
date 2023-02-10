package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.List;

public class InFavour implements ShortlistStrats{
    private List<Card> shortList = new ArrayList<>();
    private Hand playerHand;
    private Hand [] piles;
    private int playerIndex;

    public void setPlayerHand(Hand playerHand) {
        this.playerHand = playerHand;
    }

    public void setPiles(Hand[] piles) {
        this.piles = piles;
    }

    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    public InFavour(){}
    private List<Card> getValidCards(){
        List<Card> validCards = new ArrayList<>();
        for (Card card: playerHand.getCardList()) {
            for (int i =0; i<2;i++) {
                try
                {
                    Rules.validate(card,i,piles);
                    if (!validCards.contains(card)) {
                        GoTCard.Suit suit = (GoTCard.Suit) card.getSuit();
                        if (suit.isMagic()){
                            if(playerIndex == 0 || playerIndex == 2){
                                if(i == 1){
                                    validCards.add(card);
                                }
                            }
                            if(playerIndex == 1 || playerIndex == 3){
                                if(i == 0){
                                    validCards.add(card);
                                }
                            }
                        }else{
                            validCards.add(card);
                        }
                    }
                }
                catch(BrokeRuleException ex)
                {

                }
            }
        }
        return validCards;
    }

    private List<Integer> getMatchingPiles(List<Card> validCards){
        List<Integer> matchingPiles = new ArrayList<>();
        for (Card card : validCards){
            GoTCard.Suit suit = (GoTCard.Suit) card.getSuit();
            if(playerIndex == 0 || playerIndex == 2){
                if(suit.isMagic()){
                    matchingPiles.add(1);
                }else{
                    matchingPiles.add(0);
                }
            } else {
                if(suit.isMagic()){
                    matchingPiles.add(0);
                }else{
                    matchingPiles.add(1);
                }
            }
        }
        return matchingPiles;
    }


    public int[] calculatePileRanks(ArrayList<Card> pileOfCards) {
        int att = 0;
        int def = 0;
        int lastValue = 0;
        GoTCard.Suit lastSuit = GoTCard.Suit.HEARTS;

        if(pileOfCards.size()>0){
            for(int i=0; i<pileOfCards.size();i++){
                Card currCard = pileOfCards.get(i);
                if(i == 0){
                    att = ((GoTCard.Rank) currCard.getRank()).getRankValue();
                    def = ((GoTCard.Rank) currCard.getRank()).getRankValue();
                    lastValue = ((GoTCard.Rank) currCard.getRank()).getRankValue();
                    lastSuit = (GoTCard.Suit) currCard.getSuit();
                }else{
                    GoTCard.Suit currSuit = (GoTCard.Suit) currCard.getSuit();
                    int currValue = ((GoTCard.Rank) currCard.getRank()).getRankValue();
                    int value;
                    if(currValue == lastValue){
                        value = currValue * 2;
                    }else{
                        value = currValue;
                    }
                    if(currSuit.isAttack()){
                        att += value;
                    }

                    if(currSuit.isDefence()){
                        def += value;
                    }

                    if(currSuit.isMagic()){
                        if(lastSuit.isDefence()){
                            def -= value;
                        }
                        if(lastSuit.isAttack()){
                            att -= value;
                        }
                    }
                    lastValue = currValue;
                    lastSuit = currSuit;
                }

                if (att < 0) {att =0;}
                if (def < 0) {def =0;}

            }
        }

        return new int[] { att,def };
    }
    public List<Card> getShortList() {
        List<Card> validCards = getValidCards();
        List<Integer> matchingPiles = getMatchingPiles(validCards);
        for (int i = 0; i < validCards.size(); i++){
            int myPile;
            int theirPile;
            if(playerIndex == 0 || playerIndex == 2){
                myPile = 0;
                theirPile = 1;
            }else{
                theirPile = 0;
                myPile = 1;
            }

            int [] myPrevResult = calculatePileRanks(piles[myPile].getCardList());
            int [] theirPrevResult = calculatePileRanks(piles[theirPile].getCardList());

            boolean myWin1 = myPrevResult[0] > theirPrevResult[1];
            boolean myWin2 = myPrevResult[1] > theirPrevResult[0];

            ArrayList<Card> newPiles0 = new ArrayList<>();
            ArrayList<Card> newPiles1 = new ArrayList<>();
            for(Card card: piles[myPile].getCardList()){
                newPiles0.add(card);
            }
            for(Card card: piles[theirPile].getCardList()){
                newPiles1.add(card);
            }

            if(matchingPiles.get(i)==0){
                newPiles0.add(validCards.get(i));
            }else{
                newPiles1.add(validCards.get(i));
            }

            int [] myNewResult = calculatePileRanks(newPiles0);
            int [] theirNewResult = calculatePileRanks(newPiles1);

            boolean myNewWin1 = myNewResult[0] > theirNewResult[1];
            boolean myNewWin2 = myNewResult[1] > theirNewResult[0];

            if(!myWin1 && myNewWin1){
                shortList.add(validCards.get(i));
            }
            if(!myWin2 && myNewWin2){
                shortList.add(validCards.get(i));
            }
        }
        return shortList;
    }
}

