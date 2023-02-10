package src;

import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.Properties;

public class Madness extends Tetris {
    Madness(TetrisGameCallback gameCallback, Properties properties){
        super(gameCallback,properties);
        numBlocks = 10;
        setStatsDifficulty("Madness");
    }

    @Override
    protected int getSlowDown(){
        Random r = new Random();
        int high = slowDown; int low = (int)slowDown/2;
        int result = (r.nextInt(high - low) + low) ;
        return result;
    }

    @Override
    protected void moveBlock(int keyEvent) {
        switch (keyEvent) {
            case KeyEvent.VK_LEFT:
                currentBlock.left();
                break;
            case KeyEvent.VK_RIGHT:
                currentBlock.right();
                break;
            case KeyEvent.VK_DOWN:
                currentBlock.drop();
                break;
            default:
                return;
        }
    }

}