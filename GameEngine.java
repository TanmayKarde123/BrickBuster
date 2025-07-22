import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Timer; 
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.Font;
import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;

public class GameEngine extends JPanel implements KeyListener, ActionListener {

    public enum GameState {
        TITLE_SCREEN,
        PLAYING,
        GAME_OVER,
        LEVEL_COMPLETE,
        VICTORY
    }
    
    private GameState currentState = GameState.TITLE_SCREEN;
    private int playerScore = 0;
    private int currentLevel = 1;
    private int maxLevel = 3; 
    private Timer gameTimer;
    private int timerDelay = 8;

    private int paddleX = 310;
    
    // Ball
    private int ballX = 120;
    private int ballY = 350;
    private int ballXVelocity = -2;
    private int ballYVelocity = -3;
    
    private BrickField brickField;
    private int remainingBricks;

    public GameEngine() {
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        gameTimer = new Timer(timerDelay, this);
        gameTimer.start();
    }
    
    // Sound 
    private void playBrickHitSound() {
        try {
            //  brick hit 
            byte[] buf = new byte[1000];
            float frequency = 800.0f; // Higher frequency for brick hit
            for (int i = 0; i < buf.length; i++) {
                double angle = i / (44100.0 / frequency) * 2.0 * Math.PI;
                buf[i] = (byte) (Math.sin(angle) * 127.0 * Math.exp(-3.0 * i / buf.length));
            }
            
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            AudioFormat af = new AudioFormat(44100, 8, 1, true, false);
            AudioInputStream ais = new AudioInputStream(bais, af, buf.length);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
        } catch (Exception e) {
            // Sound failed, continue silently
        }
    }
    
    private void playPaddleHitSound() {
        try {
            // paddle hit 
            byte[] buf = new byte[800];
            float frequency = 400.0f; // Lower frequency for paddle hit
            for (int i = 0; i < buf.length; i++) {
                double angle = i / (44100.0 / frequency) * 2.0 * Math.PI;
                buf[i] = (byte) (Math.sin(angle) * 127.0 * Math.exp(-2.0 * i / buf.length));
            }
            
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            AudioFormat af = new AudioFormat(44100, 8, 1, true, false);
            AudioInputStream ais = new AudioInputStream(bais, af, buf.length);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
        } catch (Exception e) {
            // Sound failed, continue silently
        }
    }
    
    private void startGame() {
        currentState = GameState.PLAYING;
        playerScore = 0;
        currentLevel = 1;
        startLevel();
    }
    
    private void startLevel() {
        brickField = new BrickField(3 + currentLevel/2, 7);
        remainingBricks = countBricks();
        
        // Reset ball
        ballX = 120;
        ballY = 350;
        ballXVelocity = -2 - currentLevel/3;
        ballYVelocity = -3 - currentLevel/4;
        paddleX = 310;
    }
    
    private int countBricks() {
        int count = 0;
        for (int i = 0; i < brickField.brickGrid.length; i++) {
            for (int j = 0; j < brickField.brickGrid[0].length; j++) {
                if (brickField.brickGrid[i][j] > 0) count++;
            }
        }
        return count;
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Background gradient
        Color bgTop = new Color(15, 15, 25);
        Color bgBottom = new Color(25, 25, 35);
        g2d.setPaint(new java.awt.GradientPaint(0, 0, bgTop, 0, 592, bgBottom));
        g2d.fillRect(0, 0, 692, 592);

        switch (currentState) {
            case TITLE_SCREEN:
                drawTitleScreen(g);
                break;
            case PLAYING:
                drawGameplay(g, g2d);
                break;
            case LEVEL_COMPLETE:
                drawLevelComplete(g);
                break;
            case VICTORY:
                drawVictory(g);
                break;
            case GAME_OVER:
                drawGameOver(g);
                break;
        }

        g.dispose();
    }
    
    private void drawTitleScreen(Graphics g) {
        // Title
        g.setColor(new Color(139, 0, 0)); 
        g.setFont(new Font("Papyrus", Font.BOLD, 48));
        g.drawString("BRICK BUSTER", 180, 200);
        
        // Menu options
        g.setColor(new Color(255, 206, 100));
        g.setFont(new Font("Papyrus", Font.PLAIN, 24));
        g.drawString("Press ENTER to Play", 250, 300);
        g.drawString("Press ESC to Exit", 270, 340);
        

        
        
    }
    
    private void drawGameplay(Graphics g, Graphics2D g2d) {
        // Draw bricks
        brickField.draw(g2d);

        // Borders
        g.setColor(new Color(100, 20, 20));
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);

        // Score and level
        g.setColor(new Color(255, 200, 100));
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + playerScore, 20, 30);
        g.drawString("Level: " + currentLevel + "/3", 20, 55); // Show progress out of 3

        // Paddle
        Color paddleTop = new Color(180, 60, 60);
        Color paddleBottom = new Color(120, 30, 30);
        g2d.setPaint(new java.awt.GradientPaint(paddleX, 550, paddleTop, paddleX, 558, paddleBottom));
        g2d.fillRect(paddleX, 550, 100, 8);
        g.setColor(new Color(220, 100, 100));
        g.fillRect(paddleX, 550, 100, 2);

        // Enhanced ball with glow effect
        g.setColor(new Color(255, 100, 100, 80)); // Glow
        g.fillOval(ballX - 5, ballY - 5, 30, 30);
        g.setColor(new Color(255, 150, 150)); // Main ball
        g.fillOval(ballX, ballY, 20, 20);
        g.setColor(new Color(255, 200, 200)); // Highlight
        g.fillOval(ballX + 3, ballY + 3, 8, 8);
    }
    
    private void drawLevelComplete(Graphics g) {
        drawGameplay(g, (Graphics2D) g);
        
        g.setColor(new Color(100, 255, 100));
        g.setFont(new Font("Papyrus", Font.BOLD, 36));
        g.drawString("LEVEL COMPLETE!", 200, 280);
        g.setFont(new Font("Papyrus", Font.PLAIN, 18));
        g.drawString("Press ENTER for next level", 240, 320);
    }
    
    private void drawVictory(Graphics g) {
        g.setColor(new Color(255, 215, 0));
        g.setFont(new Font("Papyrus", Font.BOLD, 48));
        g.drawString("YOU WON!! :)", 200, 280);
        g.setFont(new Font("Papyrus", Font.PLAIN, 24));
        g.drawString("Final Score: " + playerScore, 240, 320);
        g.setFont(new Font("Papyrus", Font.PLAIN, 18));
        g.drawString("All 3 levels completed!", 250, 350);
        g.drawString("Press ENTER to restart", 250, 380);
    }
    
    private void drawGameOver(Graphics g) {
        g.setColor(new Color(220, 50, 50));
        g.setFont(new Font("Papyrus", Font.BOLD, 48));
        g.drawString("YOU LOST", 240, 280);
        g.setFont(new Font("Papyrus", Font.PLAIN, 18));
        g.drawString("Press ENTER to restart", 250, 360);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentState == GameState.PLAYING) {
            // Ball movement
            ballX += ballXVelocity;
            ballY += ballYVelocity;
            
            // Wall collisions
            if (ballX < 0 || ballX > 670) {
                ballXVelocity = -ballXVelocity;
            }
            if (ballY < 0) {
                ballYVelocity = -ballYVelocity;
            }
            
            // Paddle collision with sound
            if (new Rectangle(ballX, ballY, 20, 20).intersects(new Rectangle(paddleX, 550, 100, 8))) {
                ballYVelocity = -Math.abs(ballYVelocity);
                playPaddleHitSound(); // Play sound when ball hits paddle
            }
            
            // Brick collision
            checkBrickCollisions();
            
            // Check win/lose conditions
            if (ballY > 570) {
                currentState = GameState.GAME_OVER;
            } else if (remainingBricks <= 0) {
                if (currentLevel >= maxLevel) {
                    currentState = GameState.VICTORY;
                } else {
                    currentState = GameState.LEVEL_COMPLETE;
                }
            }
        }
        
        repaint();
    }
    
    private void checkBrickCollisions() {
        outerLoop: for (int i = 0; i < brickField.brickGrid.length; i++) {
            for (int j = 0; j < brickField.brickGrid[0].length; j++) {
                if (brickField.brickGrid[i][j] > 0) {
                    int brickX = j * brickField.brickWidth + 80;
                    int brickY = i * brickField.brickHeight + 50;
                    
                    Rectangle brickRect = new Rectangle(brickX, brickY, brickField.brickWidth, brickField.brickHeight);
                    Rectangle ballRect = new Rectangle(ballX, ballY, 20, 20);
                    
                    if (ballRect.intersects(brickRect)) {
                        brickField.setBrickValue(0, i, j);
                        remainingBricks--;
                        playerScore += 10;
                        playBrickHitSound(); // Play sound when ball hits brick
                        
                        // Ball direction change
                        if (ballX + 19 <= brickRect.x || ballX + 1 >= brickRect.x + brickRect.width) {
                            ballXVelocity = -ballXVelocity;
                        } else {
                            ballYVelocity = -ballYVelocity;
                        }
                        
                        break outerLoop;
                    }
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (currentState) {
            case TITLE_SCREEN:
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    startGame();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
                break;
                
            case PLAYING:
                if (e.getKeyCode() == KeyEvent.VK_RIGHT && paddleX < 600) {
                    paddleX += 20;
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT && paddleX > 10) {
                    paddleX -= 20;
                }
                break;
                
            case LEVEL_COMPLETE:
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    currentLevel++;
                    startLevel();
                    currentState = GameState.PLAYING;
                }
                break;
                
            case GAME_OVER:
            case VICTORY:
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    startGame();
                }
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}