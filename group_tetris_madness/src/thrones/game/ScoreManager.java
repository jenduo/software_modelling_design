package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.stream.Collectors;

public class ScoreManager {

    public final int nbPlayers = 4;
    private int[] scores = new int[nbPlayers];
    private static DisplayManager dm;
    private final String[] playerTeams = { "[Players 0 & 2]", "[Players 1 & 3]"};
    private final int ATTACK_RANK_INDEX = 0;
    private final int DEFENCE_RANK_INDEX = 1;

    private static ScoreManager instance;
    public static ScoreManager getInstance(DisplayManager dm){
        instance = new ScoreManager(dm);
        return instance;
    }


    private ScoreManager(DisplayManager dm){
        this.dm = dm;
        this.initScore();
    }

    public String canonical(GoTCard.Suit s) { return s.toString().substring(0, 1); }

    public String canonical(GoTCard.Rank r) {
        return switch (r) {
            case ACE, KING, QUEEN, JACK, TEN -> r.toString().substring(0, 1);
            default -> String.valueOf(r.getRankValue());
        };
    }

    public String canonical(Card c) {
        return canonical((GoTCard.Rank) c.getRank()) + canonical((GoTCard.Suit) c.getSuit());
    }

    public String canonical(Hand h) {
        return "[" + h.getCardList().stream().map(this::canonical).collect(Collectors.joining(",")) + "]";
    }

    public void checkWin(Hand[] piles){
        this.updatePileRanks(piles);
        int[] pile0Ranks = this.calculatePileRanks(0, piles);
        int[] pile1Ranks = this.calculatePileRanks(1, piles);
        System.out.println("piles[0]: " + canonical(piles[0]));
        System.out.println("piles[0] is " + "Attack: " + pile0Ranks[ATTACK_RANK_INDEX] + " - Defence: " + pile0Ranks[DEFENCE_RANK_INDEX]);
        System.out.println("piles[1]: " + canonical(piles[1]));
        System.out.println("piles[1] is " + "Attack: " + pile1Ranks[ATTACK_RANK_INDEX] + " - Defence: " + pile1Ranks[DEFENCE_RANK_INDEX]);
        GoTCard.Rank pile0CharacterRank = (GoTCard.Rank) piles[0].getCardList().get(0).getRank();
        GoTCard.Rank pile1CharacterRank = (GoTCard.Rank) piles[1].getCardList().get(0).getRank();
        String character0Result;
        String character1Result;

        if (pile0Ranks[ATTACK_RANK_INDEX] > pile1Ranks[DEFENCE_RANK_INDEX]) {
            scores[0] += pile1CharacterRank.getRankValue();
            scores[2] += pile1CharacterRank.getRankValue();
            character0Result = "Character 0 attack on character 1 succeeded.";
        } else {
            scores[1] += pile1CharacterRank.getRankValue();
            scores[3] += pile1CharacterRank.getRankValue();
            character0Result = "Character 0 attack on character 1 failed.";
        }

        if (pile1Ranks[ATTACK_RANK_INDEX] > pile0Ranks[DEFENCE_RANK_INDEX]) {
            scores[1] += pile0CharacterRank.getRankValue();
            scores[3] += pile0CharacterRank.getRankValue();
            character1Result = "Character 1 attack on character 0 succeeded.";
        } else {
            scores[0] += pile0CharacterRank.getRankValue();
            scores[2] += pile0CharacterRank.getRankValue();
            character1Result = "Character 1 attack character 0 failed.";
        }
        this.updateScores();
        System.out.println(character0Result);
        System.out.println(character1Result);
        dm.setStatusText(character0Result + " " + character1Result);
    }

    public void printResult(){
        String text;
        if (scores[0] > scores[1]) {
            text = "Players 0 and 2 won.";
        } else if (scores[0] == scores[1]) {
            text = "All players drew.";
        } else {
            text = "Players 1 and 3 won.";
        }
        System.out.println("Result: " + text);
        dm.setStatusText(text);
    }

    private void initScore() {
        for (int i = 0; i < nbPlayers; i++) {
            scores[i] = 0;
            dm.printScore(i);
        }
        dm.printPileText();
    }


    public int[] calculatePileRanks(int pileIndex, Hand[] piles) {
        Hand currentPile = piles[pileIndex];
        int att = 0;
        int def = 0;
        int lastValue = 0;
        GoTCard.Suit lastSuit = GoTCard.Suit.HEARTS;

        if(!currentPile.isEmpty()){
            for(int i=0; i<currentPile.getCardList().size();i++){

                Card currCard = currentPile.get(i);
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


    public void updatePileRanks(Hand[] piles) {
        for (int j = 0; j <  piles.length; j++) {
            int[] ranks = calculatePileRanks(j, piles);
            dm.updatePileRankState(j, ranks[ATTACK_RANK_INDEX], ranks[DEFENCE_RANK_INDEX]);
        }
    }

    public void updateScores() {
        for (int i = 0; i < nbPlayers; i++) {
            dm.updateScore(i, scores);
        }
        System.out.println(playerTeams[0] + " score = " + scores[0] + "; " + playerTeams[1] + " score = " + scores[1]);
    }
}
