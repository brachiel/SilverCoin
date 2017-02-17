package ch.chrummibei.silvercoin;

import ch.chrummibei.silvercoin.gui.SilverCoinComponent;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        SilverCoinComponent game = new SilverCoinComponent();

        JFrame jframe = new JFrame("SilverCoin - A space trade simulator");
        JPanel jpanel = new JPanel(new BorderLayout());
        jpanel.add(game, BorderLayout.CENTER);

        jframe.setContentPane(jpanel);
        jframe.pack();
        jframe.setLocationRelativeTo(null);
        jframe.setResizable(false);
        jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jframe.setVisible(true);

        game.start();
    }
}


