import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.GradientPaint;

public class BrickField {
    public int brickGrid[][];
    public int brickWidth;
    public int brickHeight;

    public BrickField(int rows, int columns) {
        brickGrid = new int[rows][columns];
        for (int i = 0; i < brickGrid.length; i++) {
            for (int j = 0; j < brickGrid[0].length; j++) {
                brickGrid[i][j] = 1; 
            }
        }

        brickWidth = 540 / columns;
        brickHeight = 150 / rows;
    }

    public void draw(Graphics2D g) {
        for (int i = 0; i < brickGrid.length; i++) {
            for (int j = 0; j < brickGrid[0].length; j++) {
                if (brickGrid[i][j] > 0) {
                    int x = j * brickWidth + 80;
                    int y = i * brickHeight + 50;
                    
                    // Enhanced brick with gradient
                    Color topColor = new Color(220, 50, 50);    // Bright red
                    Color bottomColor = new Color(150, 25, 25); // Darker red
                    
                    GradientPaint gradient = new GradientPaint(
                        x, y, topColor, 
                        x, y + brickHeight, bottomColor
                    );
                    
                    g.setPaint(gradient);
                    g.fillRect(x, y, brickWidth, brickHeight);
                    
                    
                    g.setColor(new Color(255, 100, 100, 180));
                    g.fillRect(x, y, brickWidth, 3);
                    
                  
                    g.setColor(new Color(80, 10, 10, 120));
                    g.fillRect(x, y + brickHeight - 3, brickWidth, 3);
                    

                    g.setStroke(new BasicStroke(2));
                    g.setColor(new Color(40, 5, 5));
                    g.drawRect(x, y, brickWidth, brickHeight);
                    
                    
                    g.setStroke(new BasicStroke(1));
                    g.setColor(new Color(255, 80, 80, 100));
                    g.drawRect(x + 2, y + 2, brickWidth - 4, brickHeight - 4);
                }
            }
        }
    }

    public void setBrickValue(int value, int row, int col) {
        brickGrid[row][col] = value;
    }
}