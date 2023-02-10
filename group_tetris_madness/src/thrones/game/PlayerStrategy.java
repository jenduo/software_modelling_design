package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.Optional;

public abstract class PlayerStrategy {
    protected boolean heartTurn(Hand[] piles){
        if(piles[0].isEmpty()||piles[1].isEmpty()){
            return true;
        }else{
            return false;
        }
    }
    public abstract Optional<Card> waitForValidCard();
    public abstract int waitForValidPile();
}
