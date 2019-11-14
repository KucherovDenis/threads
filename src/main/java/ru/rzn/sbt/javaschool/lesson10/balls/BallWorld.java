package ru.rzn.sbt.javaschool.lesson10.balls;

import java.awt.*;
import javax.swing.*;
import java.util.*;

public class BallWorld extends JPanel {

    private final int xSize = 250;
    private final int ySize = 250;

    private final static Color BGCOLOR = Color.white;


    private ArrayList<Ball> balls = new ArrayList<Ball>();

    public BallWorld() {
        setPreferredSize(new Dimension(xSize, ySize));
        setOpaque(true);
        setBackground(BGCOLOR);
    }

    public void addBall(final Ball b) {
        synchronized (this) {
            balls.add(b);
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                repaint();
            }
        });
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        synchronized (this) {
            for (Ball b : balls)
                b.draw(g);
        }
    }

    public int countBoll() {
        return balls.size();
    }

    public void remove(Ball b) {
        synchronized (this) {
            balls.remove(b);
            repaint();
        }
    }
}
