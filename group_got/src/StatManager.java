package src;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class StatManager {
    private String difficulty;
    private int currentRound;
    private int totalScore;
    private ArrayList<String> allRounds = new ArrayList<String>();
    private HashMap<String, Integer> tally = new HashMap<String, Integer>();

    public StatManager () {
        //Initialises the values
        resetHashMap();
        currentRound = 0;
        totalScore = 0;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void updatePiece (String letter){
        tally.put(letter, tally.get(letter) + 1);
    }

    private void resetHashMap(){
        tally.put("I", 0);
        tally.put("J", 0);
        tally.put("L", 0);
        tally.put("O", 0);
        tally.put("S", 0);
        tally.put("T", 0);
        tally.put("Z", 0);
        tally.put("P", 0);
        tally.put("Q", 0);
        tally.put("+", 0);
    }

    public void updateStats(int score){
        currentRound ++;
        totalScore = totalScore + score;


        allRounds.add("------------------------------------------------------------");
        allRounds.add("Round #"+currentRound);
        allRounds.add("Score: " +score);

        for(String key: tally.keySet()){
            allRounds.add(key +":"+tally.get(key));
        }
        resetHashMap();

        File fold = new File("Statistics.txt");
        fold.delete();

        try {
            FileWriter myWriter = new FileWriter("Statistics.txt");
            myWriter.write("Difficulty: " + difficulty);
            myWriter.write("\nAverage Score per round: " + (int) totalScore/currentRound);
            for (String line : allRounds){
                myWriter.write("\n"+line);
            }
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


    }


}
