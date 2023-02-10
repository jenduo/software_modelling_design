package src;

import ch.aplu.jgamegrid.Actor;
import src.utility.PropertiesLoader;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Arrays;
import java.util.Properties;

public class Driver {
    public static final String DEFAULT_PROPERTIES_PATH = "properties/test2.properties";


    /**
     * Starting point
     * @param args the command line arguments
     */

    public static void main(String args[]) {

        String propertiesPath = DEFAULT_PROPERTIES_PATH;
        String difficulty;

        if (args.length > 0) {
            propertiesPath = args[0];
        }
        final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);

        difficulty = properties.getProperty("difficulty");

        boolean isLoggingTest = Boolean.parseBoolean(properties.getProperty("logTest", "false"));
        TetrisGameCallback gameCallback = new TetrisGameCallback(isLoggingTest);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                 if (difficulty.equals("easy")){
                     new Easy (gameCallback,properties).setVisible(true);
                 }

                if (difficulty.equals("medium")){
                    new Medium (gameCallback,properties).setVisible(true);
                }

                if (difficulty.equals("madness")){
                    new Madness (gameCallback,properties).setVisible(true);
                }
            }
        });
    }
}
