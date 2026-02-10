package com.comedorucv.vistas;

import javax.swing.*;
import java.awt.*;

public class NeumorphicPanel extends JPanel {

    public NeumorphicPanel() {
        setBackground(new Color(230, 230, 230));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setLayout(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int arc = 30;
        g2.setColor(new Color(200, 200, 200));
        g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, arc, arc);
        g2.setColor(new Color(255, 255, 255));
        g2.fillRoundRect(-2, -2, getWidth() - 10, getHeight() - 10, arc, arc);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, arc, arc);
    }
}
