package com.comedorucv.vistas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class LogoPlaceholder extends JPanel {
    private int size;
    private Image logo;

    public LogoPlaceholder(int size) {
        this.size = size;
        setPreferredSize(new Dimension(size + 12, size + 12));
        setOpaque(false);
        try {
            java.net.URL res = getClass().getResource("/imagenes/Logo_Universidad_Central_de_Venezuela.png");
            if (res != null) {
                Image img = ImageIO.read(res);
                if (img != null) {
                    logo = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
                    return;
                }
            }
        } catch (IOException e) {
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int x = 6;
        int y = (getHeight() - size) / 2;

        if (logo != null) {
            java.awt.geom.Ellipse2D circle = new java.awt.geom.Ellipse2D.Float(x, y, size, size);
            g2.setClip(circle);
            int imgW = logo.getWidth(null);
            int imgH = logo.getHeight(null);
            int drawX = x + (size - imgW) / 2;
            int drawY = y + (size - imgH) / 2;
            g2.drawImage(logo, drawX, drawY, null);
            g2.setClip(null);
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(new Color(200, 200, 200));
            g2.draw(circle);
        } else {
            g2.setColor(Color.WHITE);
            g2.fillOval(x, y, size, size);
            g2.setColor(new Color(200, 200, 200));
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(x, y, size, size);
        }
        g2.dispose();
    }
}
