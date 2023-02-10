package src;

import java.util.Properties;

public class Easy extends Tetris{
    Easy(TetrisGameCallback gameCallback, Properties properties) {
        super(gameCallback, properties);
        numBlocks = 7;
        setStatsDifficulty("Easy");
    }
}
