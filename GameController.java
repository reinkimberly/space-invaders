import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameController {
    private final GameModel model;
    private final GameView view;
    private Timer gameTimer;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
    }

    private void createAndShowGui() {
        JFrame frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(view);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        view.setFocusable(true);
        view.requestFocusInWindow();
        view.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        gameTimer = new Timer(33, e -> {
            model.updateGameTick();
            view.repaint();
            if (model.isGameOver()) {
                gameTimer.stop();
            }
        });
        gameTimer.start();
    }

    private void handleKeyPress(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                model.movePlayerLeft();
                break;
            case KeyEvent.VK_RIGHT:
                model.movePlayerRight();
                break;
            case KeyEvent.VK_SPACE:
                model.firePlayerBullet();
                break;
            case KeyEvent.VK_C:
                if (model.isLevelTransition()) {
                    model.proceedToNextLevel();
                }
                break;
        }
        view.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameModel model = new GameModel();
            GameView view = new GameView(model);
            GameController controller = new GameController(model, view);
            controller.createAndShowGui();
        });
    }
}
