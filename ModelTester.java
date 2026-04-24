import java.lang.reflect.Field;
import java.util.List;

public class ModelTester {
    public static void main(String[] args) {
        run("Player cannot move past left edge", ModelTester::testPlayerCannotMovePastLeftEdge);
        run("Player cannot move past right edge", ModelTester::testPlayerCannotMovePastRightEdge);
        run("Firing while bullet is already in flight does nothing", ModelTester::testFireWhileBulletInFlightDoesNothing);
        run("Bullet that reaches the top is removed", ModelTester::testBulletReachesTopIsRemoved);
        run("Destroying an alien increases the score", ModelTester::testDestroyingAlienIncreasesScore);
        run("Losing all lives triggers game-over", ModelTester::testLosingAllLivesTriggersGameOver);
    }

    private static void run(String name, TestCase testCase) {
        boolean result;
        try {
            result = testCase.run();
        } catch (Exception e) {
            result = false;
            System.out.println(name + ": FAIL (exception: " + e + ")");
            e.printStackTrace(System.out);
            return;
        }
        System.out.println(name + ": " + (result ? "PASS" : "FAIL"));
    }

    private static boolean testPlayerCannotMovePastLeftEdge() {
        GameModel model = new GameModel();
        for (int i = 0; i < 100; i++) {
            model.movePlayerLeft();
        }
        return model.getPlayerX() == 0;
    }

    private static boolean testPlayerCannotMovePastRightEdge() {
        GameModel model = new GameModel();
        for (int i = 0; i < 200; i++) {
            model.movePlayerRight();
        }
        return model.getPlayerX() == GameModel.GAME_WIDTH - model.getPlayerWidth();
    }

    private static boolean testFireWhileBulletInFlightDoesNothing() {
        GameModel model = new GameModel();
        model.firePlayerBullet();
        if (!model.isPlayerBulletActive()) {
            return false;
        }

        int initialX = model.getPlayerBulletX();
        int initialY = model.getPlayerBulletY();

        model.firePlayerBullet();
        return model.isPlayerBulletActive()
                && model.getPlayerBulletX() == initialX
                && model.getPlayerBulletY() == initialY;
    }

    private static boolean testBulletReachesTopIsRemoved() {
        GameModel model = new GameModel();
        model.firePlayerBullet();
        for (int i = 0; i < 200 && model.isPlayerBulletActive(); i++) {
            model.updateGameTick();
        }
        return !model.isPlayerBulletActive();
    }

    private static boolean testDestroyingAlienIncreasesScore() {
        GameModel model = new GameModel();
        int initialScore = model.getScore();
        int targetColumn = 4;
        int targetCenterX = model.getAlienX(targetColumn) + model.getAlienWidth() / 2;

        while (model.getPlayerX() + model.getPlayerWidth() / 2 < targetCenterX) {
            model.movePlayerRight();
        }
        while (model.getPlayerX() + model.getPlayerWidth() / 2 > targetCenterX) {
            model.movePlayerLeft();
        }

        if (model.getPlayerX() + model.getPlayerWidth() / 2 != targetCenterX) {
            return false;
        }

        model.firePlayerBullet();
        for (int i = 0; i < 500 && model.isPlayerBulletActive(); i++) {
            model.updateGameTick();
        }
        return model.getScore() > initialScore;
    }

    private static boolean testLosingAllLivesTriggersGameOver() throws Exception {
        GameModel model = new GameModel();
        setPrivateField(model, "lives", 1);

        @SuppressWarnings("unchecked")
        List<GameModel.Bullet> bullets = (List<GameModel.Bullet>) getPrivateField(model, "alienBullets");
        int bulletX = model.getPlayerX() + model.getPlayerWidth() / 2;
        int bulletY = model.getPlayerY();
        bullets.add(new GameModel.Bullet(bulletX, bulletY, 6));

        model.updateGameTick();
        return model.getLives() == 0 && model.isGameOver();
    }

    private static Object getPrivateField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private interface TestCase {
        boolean run() throws Exception;
    }
}
