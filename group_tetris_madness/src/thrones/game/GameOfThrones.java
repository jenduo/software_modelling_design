package thrones.game;

// Oh_Heaven.java

import ch.aplu.jcardgame.*;

import java.util.*;
import java.util.stream.Collectors;


@SuppressWarnings("serial")
public class GameOfThrones {
    private static PlayerFacade facade;

    static public int seed;
    static Random random;
    private DisplayManager dm;
    private ScoreManager sm;
    private Board board;


    public String canonical(GoTCard.Suit s) { return s.toString().substring(0, 1); }

    public String canonical(GoTCard.Rank r) {
        switch (r) {
            case ACE: case KING: case QUEEN: case JACK: case TEN:
                return r.toString().substring(0, 1);
            default:
                return String.valueOf(r.getRankValue());
        }
    }

    public String canonical(Card c) {
        return canonical((GoTCard.Rank) c.getRank()) + canonical((GoTCard.Suit) c.getSuit());
    }


    // return random Card from Hand
    public static Card randomCard(Hand hand) {
        assert !hand.isEmpty() : " random card from empty hand.";
        int x = random.nextInt(hand.getNumberOfCards());
        return hand.get(x);
    }

    public final int nbPlayers = 4;
    public final int nbStartCards = 9;
	public final int nbPlays = 6;
	public final int nbRounds = 3;

    private Deck deck = new Deck(GoTCard.Suit.values(), GoTCard.Rank.values(), "cover");


    static int watchingTime;
    private Hand[] hands;
    private Hand[] piles;
    private int nextStartingPlayer = random.nextInt(nbPlayers);


    private int[] scores = new int[nbPlayers];


    private Optional<Card> selected;


    private final int NON_SELECTION_VALUE = -1;
    private int selectedPileIndex = NON_SELECTION_VALUE;


    private int getPlayerIndex(int index) {
        return index % nbPlayers;
    }

    void resetPile() {
        if (piles != null) {
            facade.updateFacade(piles);
            for (Hand pile : piles) {
                pile.removeAll(true);
            }
        }
        piles = new Hand[2];
        for (int i = 0; i < 2; i++) {
            piles[i] = new Hand(deck);

            dm.displayPile(piles[i],i);
        }

        sm.updatePileRanks(piles);
    }


    private void executeAPlay() {
        resetPile();

        nextStartingPlayer = getPlayerIndex(nextStartingPlayer);
        if (hands[nextStartingPlayer].getNumberOfCardsWithSuit(GoTCard.Suit.HEARTS) == 0)
            nextStartingPlayer = getPlayerIndex(nextStartingPlayer + 1);
        assert hands[nextStartingPlayer].getNumberOfCardsWithSuit(GoTCard.Suit.HEARTS) != 0 : " Starting player has no hearts.";

        // 1: play the first 2 hearts
        for (int i = 0; i < 2; i++) {
            int playerIndex = getPlayerIndex(nextStartingPlayer + i);
            dm.setStatusText("Player " + playerIndex + " select a Heart card to play");
            selected = facade.getCard(playerIndex, piles, hands[playerIndex], this.dm);
            int pileIndex = playerIndex % 2;
            assert selected.isPresent() : " Pass returned on selection of character.";
            System.out.println("Player " + playerIndex + " plays " + canonical(selected.get()) + " on pile " + pileIndex);
            selected.get().setVerso(false);
            selected.get().transfer(piles[pileIndex], true); // transfer to pile (includes graphic effect)
            sm.updatePileRanks(piles);
        }

        // 2: play the remaining nbPlayers * nbRounds - 2
        int remainingTurns = nbPlayers * nbRounds - 2;
        int nextPlayer = nextStartingPlayer + 2;

        while(remainingTurns > 0) {
            nextPlayer = getPlayerIndex(nextPlayer);
            dm.setStatusText("Player" + nextPlayer + " select a non-Heart card to play.");
            selected = facade.getCard(nextPlayer,piles,hands[nextPlayer],this.dm);
            if (selected.isPresent()) {
                dm.setStatusText("Selected: " + canonical(selected.get()) + ". Player" + nextPlayer + " select a pile to play the card.");
                selectedPileIndex = facade.getPile(nextPlayer,selected.get(),piles,this.dm);
                try {
                    Rules.validate(selected.get(),selectedPileIndex,piles);
                }catch (BrokeRuleException ex){
                    System.out.println("Illegal Move");
                    break;
                }
                System.out.println("Player " + nextPlayer + " plays " + canonical(selected.get()) + " on pile " + selectedPileIndex);
                selected.get().setVerso(false);
                selected.get().transfer(piles[selectedPileIndex], true); // transfer to pile (includes graphic effect)
                sm.updatePileRanks(piles);
            } else {
                dm.setStatusText("Pass.");
            }
            nextPlayer++;
            remainingTurns--;
        }

        // 3: calculate winning & update scores for players
        sm.checkWin(piles);


        // 5: discarded all cards on the piles
        nextStartingPlayer += 1;
        dm.delay(watchingTime);
    }

    public GameOfThrones() {
        dm =  DisplayManager.getInstance();
        sm = ScoreManager.getInstance(dm);
        board = new Board(nbStartCards, nbPlayers, deck, dm);
        hands = board.setupGame();
        for (int i = 0; i < nbPlays; i++) {
            executeAPlay();
            sm.updateScores();
        }
        sm.printResult();
        dm.refresh();
    }

    public static void main(String[] args) {
        final String DEFAULT_PROPERTIES_PATH = "properties/got.properties";
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        Properties properties = new Properties();
        properties.setProperty("watchingTime", "5000");

        if (args == null || args.length == 0) {
            properties = PropertiesLoader.loadPropertiesFile(DEFAULT_PROPERTIES_PATH);
        } else {
            properties = PropertiesLoader.loadPropertiesFile(args[0]);
        }

        String seedProp = properties.getProperty("seed");  //Seed property
        if (seedProp != null) { // Use property seed
            GameOfThrones.seed = Integer.parseInt(seedProp);
        } else { // and no property
            GameOfThrones.seed = new Random().nextInt(); // so randomise
        }

        String watchTProp = properties.getProperty("watchingTime");  //Seed property
        if (seedProp != null) { // Use property seed
            GameOfThrones.watchingTime = Integer.parseInt(watchTProp);
        } else { // and no property
            GameOfThrones.watchingTime = 5000;
        }

        GameOfThrones.facade = new PlayerFacade(properties);
        System.out.println("Seed = " + GameOfThrones.seed);
        GameOfThrones.random = new Random(GameOfThrones.seed);
        new GameOfThrones();
    }

}
