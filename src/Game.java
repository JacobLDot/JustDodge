import javax.swing.*;
public class Game {
    public static final int MAP_WIDTH = 2000;
    public static final int MAP_HEIGHT = 2000;

    public static void main(String[] args) {
        int playerStartX = MAP_WIDTH / 2;
        int playerStartY = (int)(MAP_HEIGHT * 0.7805);
        Player player = new Player(playerStartX, playerStartY);

        JFrame frame = new JFrame("Just Dodge");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);

        GameView window = new GameView(player);
        frame.add(window);
        frame.setVisible(true);
        window.requestFocusInWindow();
    }
}