package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.Optional;
import java.util.Properties;

public class PlayerFacade {
    private String[] playerTypes = new String[4];

    private HumanStrategy human = new HumanStrategy();
    private RandomStrategy random = new RandomStrategy();
    private SimpleStrategy simple = new SimpleStrategy();
    private SmartStrategy smart = new SmartStrategy();

    public PlayerFacade(Properties properties){
        playerTypes[0] = properties.getProperty("players.0");
        playerTypes[1] = properties.getProperty("players.1");
        playerTypes[2] = properties.getProperty("players.2");
        playerTypes[3] = properties.getProperty("players.3");
    }

    public Optional<Card> getCard(int nextPlayer, Hand[] piles, Hand hand, DisplayManager dm){
        switch(playerTypes[nextPlayer]) {
            case "human":
                human.setPlayerHand(hand);
                human.setPiles(piles);
                human.setDm(dm);
                return human.waitForValidCard();
            case "random":
                random.setPlayerHand(hand);
                random.setPiles(piles);
                return random.waitForValidCard();
            case "simple":
                simple.setPlayerHand(hand);
                simple.setPiles(piles);
                simple.setPlayerIndex(nextPlayer);
                return simple.waitForValidCard();
            case "smart":
                smart.setPlayerHand(hand);
                smart.setPiles(piles);
                smart.setPlayerIndex(nextPlayer);
                return smart.waitForValidCard();
        }
        return Optional.empty();
    }

    public int getPile(int nextPlayer, Card selected, Hand[] piles,DisplayManager dm){
        switch(playerTypes[nextPlayer]) {
            case "human":
                human.setPiles(piles);
                human.setFinSelected(selected);
                human.setDm(dm);
                return human.waitForValidPile();
            case "random":
                random.setPiles(piles);
                random.setFinSelected(selected);
                return random.waitForValidPile();
            case "simple":
                simple.setPiles(piles);
                simple.setFinSelected(selected);
                return simple.waitForValidPile();
            case "smart":
                smart.setPiles(piles);
                smart.setFinSelected(selected);
                return smart.waitForValidPile();
        }
        return -1;
    }

    public void updateFacade(Hand[] piles){
        smart.updateSmart(piles);
    }

}
