import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameModel {
    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;

    private static final int PLAYER_WIDTH = 40;
    private static final int PLAYER_HEIGHT = 20;
    private static final int PLAYER_Y = GAME_HEIGHT - 60;
    private static final int PLAYER_SPEED = 8;

    private static final int ALIEN_ROWS = 5;
    private static final int ALIEN_COLUMNS = 11;
    private static final int ALIEN_WIDTH = 32;
    private static final int ALIEN_HEIGHT = 24;
    private static final int ALIEN_SPACING_X = 50;
    private static final int ALIEN_SPACING_Y = 40;
    private static final int ALIEN_START_X = 80;
    private static final int ALIEN_START_Y = 60;
    private static final int BASE_ALIEN_STEP_X = 8;
    private static final int ALIEN_STEP_Y = 20;
    private static final int MAX_ALIEN_BULLETS = 3;
    private static final int ALIEN_FIRE_PERCENT = 8;
    private static final int BASE_TIMER_INTERVAL = 100;
    private static final int MIN_TIMER_INTERVAL = 25;
    private static final int TIMER_INTERVAL_DECREMENT = 2;
    private static final int MAX_LEVEL = 10;

    private static final int SHIELD_COUNT = 4;
    private static final int SHIELD_WIDTH = 80;
    private static final int SHIELD_HEIGHT = 24;
    private static final int SHIELD_HEALTH = 3;
    private static final int SHIELD_Y = PLAYER_Y - 150;

    private int playerX;
    private boolean[][] aliens;
    private int alienOffsetX;
    private int alienOffsetY;
    private int alienDirection;
    private final int initialAlienCount = ALIEN_ROWS * ALIEN_COLUMNS;
    private int currentLevel;
    private boolean levelTransition;

    private boolean playerBulletActive;
    private int playerBulletX;
    private int playerBulletY;

    private final List<Bullet> alienBullets = new ArrayList<>();
    private final List<Shield> shields = new ArrayList<>();
    private final Random random = new Random();

    private int score;
    private int lives;
    private boolean gameOver;
    private boolean gameWon;
    private static int highScore = 0;

    public GameModel() {
        resetGame();
    }

    private void resetGame() {
        resetLevelBoard();
        score = 0;
        lives = 3;
        currentLevel = 1;
        levelTransition = false;
        gameOver = false;
        gameWon = false;
    }

    private void resetLevelBoard() {
        playerX = (GAME_WIDTH - PLAYER_WIDTH) / 2;
        aliens = new boolean[ALIEN_ROWS][ALIEN_COLUMNS];
        for (int row = 0; row < ALIEN_ROWS; row++) {
            for (int col = 0; col < ALIEN_COLUMNS; col++) {
                aliens[row][col] = true;
            }
        }
        alienOffsetX = 0;
        alienOffsetY = 0;
        alienDirection = 1;
        playerBulletActive = false;
        alienBullets.clear();
        shields.clear();
        initializeShields();
    }

    private void initializeShields() {
        int spacing = (GAME_WIDTH - (SHIELD_COUNT * SHIELD_WIDTH)) / (SHIELD_COUNT + 1);
        for (int i = 0; i < SHIELD_COUNT; i++) {
            int x = spacing + i * (SHIELD_WIDTH + spacing);
            shields.add(new Shield(x, SHIELD_Y, SHIELD_WIDTH, SHIELD_HEIGHT, SHIELD_HEALTH));
        }
    }

    public void movePlayerLeft() {
        if (gameOver) {
            return;
        }
        playerX = Math.max(0, playerX - PLAYER_SPEED);
    }

    public void movePlayerRight() {
        if (gameOver) {
            return;
        }
        playerX = Math.min(GAME_WIDTH - PLAYER_WIDTH, playerX + PLAYER_SPEED);
    }

    public void firePlayerBullet() {
        if (gameOver || playerBulletActive) {
            return;
        }
        playerBulletActive = true;
        playerBulletX = playerX + PLAYER_WIDTH / 2;
        playerBulletY = PLAYER_Y;
    }

    public void updateGameTick() {
        if (gameOver || levelTransition) {
            return;
        }

        moveAliens();
        updatePlayerBullet();
        updateAlienBullets();
        fireAlienBulletsRandomly();
        detectCollisions();

        if (lives <= 0) {
            gameOver = true;
            updateHighScore();
            return;
        }

        if (aliensOffScreen()) {
            startLevelTransition();
            return;
        }

        if (allAliensDestroyed()) {
            startLevelTransition();
        }
    }

    private void moveAliens() {
        int leftmost = findLeftmostAlienColumn();
        int rightmost = findRightmostAlienColumn();

        if (leftmost < 0 || rightmost < 0) {
            return; // no aliens remain
        }

        int leftEdge = ALIEN_START_X + alienOffsetX + leftmost * ALIEN_SPACING_X;
        int rightEdge = ALIEN_START_X + alienOffsetX + rightmost * ALIEN_SPACING_X + ALIEN_WIDTH;

        int currentStepX = getCurrentAlienStepX();
        boolean shouldDescend = false;
        if (alienDirection > 0 && rightEdge + currentStepX > GAME_WIDTH) {
            shouldDescend = true;
        } else if (alienDirection < 0 && leftEdge - currentStepX < 0) {
            shouldDescend = true;
        }

        if (shouldDescend) {
            alienOffsetY += ALIEN_STEP_Y;
            alienDirection *= -1;
        } else {
            alienOffsetX += alienDirection * currentStepX;
        }
    }

    private int findLeftmostAlienColumn() {
        for (int col = 0; col < ALIEN_COLUMNS; col++) {
            for (int row = 0; row < ALIEN_ROWS; row++) {
                if (aliens[row][col]) {
                    return col;
                }
            }
        }
        return -1;
    }

    private int findRightmostAlienColumn() {
        for (int col = ALIEN_COLUMNS - 1; col >= 0; col--) {
            for (int row = 0; row < ALIEN_ROWS; row++) {
                if (aliens[row][col]) {
                    return col;
                }
            }
        }
        return -1;
    }

    private void updatePlayerBullet() {
        if (!playerBulletActive) {
            return;
        }
        playerBulletY -= 10;
        if (playerBulletY < 0) {
            playerBulletActive = false;
        }
    }

    private void updateAlienBullets() {
        for (int i = alienBullets.size() - 1; i >= 0; i--) {
            Bullet bullet = alienBullets.get(i);
            bullet.move();
            if (bullet.getY() > GAME_HEIGHT) {
                alienBullets.remove(i);
            }
        }
    }

    private void fireAlienBulletsRandomly() {
        if (alienBullets.size() >= MAX_ALIEN_BULLETS) {
            return;
        }
        if (random.nextInt(100) >= getCurrentAlienFirePercent()) {
            return;
        }

        int col = chooseRandomAlienColumn();
        if (col < 0) {
            return;
        }

        int row = findBottomAlienRowInColumn(col);
        if (row < 0) {
            return;
        }

        int x = ALIEN_START_X + alienOffsetX + col * ALIEN_SPACING_X + ALIEN_WIDTH / 2;
        int y = ALIEN_START_Y + alienOffsetY + row * ALIEN_SPACING_Y + ALIEN_HEIGHT;
        alienBullets.add(new Bullet(x, y, 6));
    }

    private int chooseRandomAlienColumn() {
        List<Integer> columnsWithAliens = new ArrayList<>();
        for (int col = 0; col < ALIEN_COLUMNS; col++) {
            if (findBottomAlienRowInColumn(col) >= 0) {
                columnsWithAliens.add(col);
            }
        }
        if (columnsWithAliens.isEmpty()) {
            return -1;
        }
        return columnsWithAliens.get(random.nextInt(columnsWithAliens.size()));
    }

    private int findBottomAlienRowInColumn(int column) {
        for (int row = ALIEN_ROWS - 1; row >= 0; row--) {
            if (aliens[row][column]) {
                return row;
            }
        }
        return -1;
    }

    private void detectCollisions() {
        if (playerBulletActive) {
            if (checkPlayerBulletHitsShield()) {
                playerBulletActive = false;
            } else {
                for (int row = 0; row < ALIEN_ROWS; row++) {
                    for (int col = 0; col < ALIEN_COLUMNS; col++) {
                        if (!aliens[row][col]) {
                            continue;
                        }
                        if (bulletHitsAlien(playerBulletX, playerBulletY, row, col)) {
                            aliens[row][col] = false;
                            playerBulletActive = false;
                            score += 10;
                            updateHighScore();
                            break;
                        }
                    }
                    if (!playerBulletActive) {
                        break;
                    }
                }
            }
        }

        for (int i = alienBullets.size() - 1; i >= 0; i--) {
            Bullet bullet = alienBullets.get(i);
            if (checkAlienBulletHitsShield(bullet)) {
                alienBullets.remove(i);
                continue;
            }
            if (bulletHitsPlayer(bullet)) {
                alienBullets.remove(i);
                lives = Math.max(0, lives - 1);
                if (lives <= 0) {
                    gameOver = true;
                }
            }
        }
    }

    private boolean bulletHitsAlien(int bulletX, int bulletY, int row, int col) {
        int alienLeft = ALIEN_START_X + alienOffsetX + col * ALIEN_SPACING_X;
        int alienTop = ALIEN_START_Y + alienOffsetY + row * ALIEN_SPACING_Y;
        int alienRight = alienLeft + ALIEN_WIDTH;
        int alienBottom = alienTop + ALIEN_HEIGHT;
        return bulletX >= alienLeft && bulletX <= alienRight && bulletY >= alienTop && bulletY <= alienBottom;
    }

    public int getRecommendedTimerInterval() {
        int destroyedAliens = initialAlienCount - getAliveAlienCount();
        int levelBonus = (currentLevel - 1) * 6;
        int interval = BASE_TIMER_INTERVAL - levelBonus - destroyedAliens * TIMER_INTERVAL_DECREMENT;
        return Math.max(MIN_TIMER_INTERVAL, interval);
    }

    private int getCurrentAlienStepX() {
        int destroyedAliens = initialAlienCount - getAliveAlienCount();
        return BASE_ALIEN_STEP_X + currentLevel + destroyedAliens / 3;
    }

    private int getCurrentAlienFirePercent() {
        return Math.min(50, ALIEN_FIRE_PERCENT + (currentLevel - 1) * 3);
    }

    public boolean isLevelTransition() {
        return levelTransition;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    private void startLevelTransition() {
        levelTransition = true;
    }

    public void proceedToNextLevel() {
        if (!levelTransition) {
            return;
        }
        levelTransition = false;
        advanceLevel();
    }

    private void advanceLevel() {
        if (currentLevel >= MAX_LEVEL) {
            gameOver = true;
            gameWon = true;
            updateHighScore();
            return;
        }

        currentLevel++;
        lives = 3; // Reset lives for new level
        resetLevelBoard();
    }

    private int getAliveAlienCount() {
        int count = 0;
        for (int row = 0; row < ALIEN_ROWS; row++) {
            for (int col = 0; col < ALIEN_COLUMNS; col++) {
                if (aliens[row][col]) {
                    count++;
                }
            }
        }
        return count;
    }

    private void updateHighScore() {
        if (score > highScore) {
            highScore = score;
        }
    }

    private boolean checkPlayerBulletHitsShield() {
        for (int i = shields.size() - 1; i >= 0; i--) {
            Shield shield = shields.get(i);
            if (bulletHitsShield(playerBulletX, playerBulletY, shield)) {
                shield.damage();
                if (shield.isDestroyed()) {
                    shields.remove(i);
                }
                return true;
            }
        }
        return false;
    }

    private boolean checkAlienBulletHitsShield(Bullet bullet) {
        for (int i = shields.size() - 1; i >= 0; i--) {
            Shield shield = shields.get(i);
            if (bulletHitsShield(bullet.getX(), bullet.getY(), shield)) {
                shield.damage();
                if (shield.isDestroyed()) {
                    shields.remove(i);
                }
                return true;
            }
        }
        return false;
    }

    private boolean bulletHitsShield(int bulletX, int bulletY, Shield shield) {
        return bulletX >= shield.getX()
                && bulletX <= shield.getX() + shield.getWidth()
                && bulletY >= shield.getY()
                && bulletY <= shield.getY() + shield.getHeight();
    }

    private boolean bulletHitsPlayer(Bullet bullet) {
        int bulletX = bullet.getX();
        int bulletY = bullet.getY();
        int playerLeft = playerX;
        int playerRight = playerX + PLAYER_WIDTH;
        int playerBottom = PLAYER_Y + PLAYER_HEIGHT;
        return bulletX >= playerLeft && bulletX <= playerRight && bulletY >= PLAYER_Y && bulletY <= playerBottom;
    }

    private boolean allAliensDestroyed() {
        for (int row = 0; row < ALIEN_ROWS; row++) {
            for (int col = 0; col < ALIEN_COLUMNS; col++) {
                if (aliens[row][col]) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean aliensOffScreen() {
        for (int row = 0; row < ALIEN_ROWS; row++) {
            for (int col = 0; col < ALIEN_COLUMNS; col++) {
                if (!aliens[row][col]) {
                    continue;
                }
                int alienBottom = ALIEN_START_Y + alienOffsetY + row * ALIEN_SPACING_Y + ALIEN_HEIGHT;
                if (alienBottom > GAME_HEIGHT) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return PLAYER_Y;
    }

    public int getPlayerWidth() {
        return PLAYER_WIDTH;
    }

    public int getPlayerHeight() {
        return PLAYER_HEIGHT;
    }

    public boolean isPlayerBulletActive() {
        return playerBulletActive;
    }

    public int getPlayerBulletX() {
        return playerBulletX;
    }

    public int getPlayerBulletY() {
        return playerBulletY;
    }

    public List<Bullet> getAlienBullets() {
        return Collections.unmodifiableList(alienBullets);
    }

    public boolean isAlienAt(int row, int col) {
        if (row < 0 || row >= ALIEN_ROWS || col < 0 || col >= ALIEN_COLUMNS) {
            return false;
        }
        return aliens[row][col];
    }

    public List<Shield> getShields() {
        return Collections.unmodifiableList(shields);
    }

    public int getAlienX(int column) {
        return ALIEN_START_X + alienOffsetX + column * ALIEN_SPACING_X;
    }

    public int getAlienY(int row) {
        return ALIEN_START_Y + alienOffsetY + row * ALIEN_SPACING_Y;
    }

    public int getAlienWidth() {
        return ALIEN_WIDTH;
    }

    public int getAlienHeight() {
        return ALIEN_HEIGHT;
    }

    public int getScore() {
        return score;
    }

    public int getHighScore() {
        return highScore;
    }

    public int getLives() {
        return lives;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public static void main(String[] args) {
        GameController.main(args);
    }

    public static class Shield {
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private int health;

        public Shield(int x, int y, int width, int height, int health) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.health = health;
        }

        public void damage() {
            health = Math.max(0, health - 1);
        }

        public boolean isDestroyed() {
            return health <= 0;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getHealth() {
            return health;
        }
    }

    public static class Bullet {
        private final int x;
        private int y;
        private final int dy;

        public Bullet(int x, int y, int dy) {
            this.x = x;
            this.y = y;
            this.dy = dy;
        }

        public void move() {
            y += dy;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}
