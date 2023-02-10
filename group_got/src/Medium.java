package src;

import java.util.Properties;
import java.util.Random;

public class Medium extends Tetris {
    Medium(TetrisGameCallback gameCallback, Properties properties){
        super(gameCallback,properties);
        numBlocks = 10;
        setStatsDifficulty("Medium");
    }

    @Override
    protected int getSlowDown(){
        return (int) ((double) slowDown /(1.2));
    }
}
