import javax.swing.*;
public class Game {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Just Dodge");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);

        GameView window = new GameView();
        frame.add(window);
        frame.setVisible(true);
        window.requestFocusInWindow();
    }
}