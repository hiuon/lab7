package bsu.rfe.java.group8.lab7.SHUDEYKO.var10;

import javax.swing.*;

public class MainFrame extends JFrame {
    public static void main(String args[]){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final MainFrame frame = new MainFrame();
                frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}
