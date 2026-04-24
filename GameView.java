import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

public class GameView extends JPanel {
    private final GameModel model;

    public GameView(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        drawBackground(g2);
        drawAliens(g2);
        drawShields(g2);
        drawPlayer(g2);
        drawBullets(g2);
        drawHud(g2);

        if (model.isLevelTransition()) {
            drawLevelTransition(g2);
        } else if (model.isGameWon()) {
            drawVictory(g2);
        } else if (model.isGameOver()) {
            drawGameOver(g2);
        }
    }

    private void drawBackground(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawPlayer(Graphics2D g2) {
        g2.setColor(Color.GREEN);
        int x = model.getPlayerX();
        int y = model.getPlayerY();
        int width = model.getPlayerWidth();
        int height = model.getPlayerHeight();
        g2.fillRect(x, y, width, height);
    }

    private void drawAliens(Graphics2D g2) {
        g2.setColor(Color.CYAN);
        int rows = 5;
        int cols = 11;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (!model.isAlienAt(row, col)) {
                    continue;
                }
                int x = model.getAlienX(col);
                int y = model.getAlienY(row);
                int width = model.getAlienWidth();
                int height = model.getAlienHeight();
                g2.fillRect(x, y, width, height);
            }
        }
    }

    private void drawBullets(Graphics2D g2) {
        if (model.isPlayerBulletActive()) {
            g2.setColor(Color.YELLOW);
            int x = model.getPlayerBulletX() - 2;
            int y = model.getPlayerBulletY();
            g2.fillRect(x, y, 4, 12);
        }

        g2.setColor(Color.RED);
        for (GameModel.Bullet bullet : model.getAlienBullets()) {
            int x = bullet.getX() - 2;
            int y = bullet.getY();
            g2.fillRect(x, y, 4, 12);
        }
    }

    private void drawHud(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        String highScoreText = "High Score: " + model.getHighScore();
        String levelText = "Level: " + model.getCurrentLevel();
        String scoreText = "Score: " + model.getScore();
        String livesText = "Lives: " + model.getLives();
        g2.drawString(highScoreText, 20, 28);
        g2.drawString(levelText, 20, 52);
        g2.drawString(scoreText, 20, 76);
        g2.drawString(livesText, 20, 100);
    }

    private void drawShields(Graphics2D g2) {
        for (GameModel.Shield shield : model.getShields()) {
            int health = shield.getHealth();
            float ratio = Math.max(0, Math.min(health / 3f, 1f));
            int red = Math.min(255, 255 - (int) (ratio * 64));
            int green = Math.min(255, (int) (ratio * 255));
            int blue = Math.max(0, 64 - (int) (ratio * 32));
            g2.setColor(new Color(red, green, blue));
            g2.fillRect(shield.getX(), shield.getY(), shield.getWidth(), shield.getHeight());
        }
    }

    private void drawLevelTransition(Graphics2D g2) {
        int nextLevel = model.getCurrentLevel() + 1;
        String message = "Next Level: " + nextLevel;
        String instruction = "Press 'C' to continue";
        g2.setColor(new Color(255, 255, 255, 230));
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
        FontMetrics metrics = g2.getFontMetrics();
        int messageWidth = metrics.stringWidth(message);
        int messageHeight = metrics.getHeight();
        int x = (getWidth() - messageWidth) / 2;
        int y = (getHeight() - messageHeight) / 2 + metrics.getAscent();
        g2.drawString(message, x, y);

        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        FontMetrics instructionMetrics = g2.getFontMetrics();
        int instructionWidth = instructionMetrics.stringWidth(instruction);
        int instructionX = (getWidth() - instructionWidth) / 2;
        int instructionY = y + messageHeight + 20;
        g2.drawString(instruction, instructionX, instructionY);
    }

    private void drawVictory(Graphics2D g2) {
        String message = "Congratulations You Beat the Aliens!";
        g2.setColor(new Color(255, 255, 255, 230));
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));
        FontMetrics metrics = g2.getFontMetrics();
        int textWidth = metrics.stringWidth(message);
        int textHeight = metrics.getHeight();
        int x = (getWidth() - textWidth) / 2;
        int y = (getHeight() - textHeight) / 2 + metrics.getAscent();
        g2.drawString(message, x, y);
    }

    private void drawGameOver(Graphics2D g2) {
        String message = "GAME OVER";
        g2.setColor(new Color(255, 255, 255, 230));
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
        FontMetrics metrics = g2.getFontMetrics();
        int textWidth = metrics.stringWidth(message);
        int textHeight = metrics.getHeight();
        int x = (getWidth() - textWidth) / 2;
        int y = (getHeight() - textHeight) / 2 + metrics.getAscent();
        g2.drawString(message, x, y);
    }
}
