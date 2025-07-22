import javax.swing.JFrame;
import java.awt.Color;

public class BrickBusterGame {
    public static void main(String[] args) {
        JFrame gameWindow = new JFrame();
        GameEngine gameEngine = new GameEngine();

        gameWindow.setBounds(10, 10, 700, 600);
        gameWindow.setTitle("Brick Buster");
        gameWindow.setResizable(false);
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameWindow.getContentPane().setBackground(new Color(20, 20, 30));

        gameWindow.add(gameEngine);
        gameWindow.setVisible(true);
    }
}