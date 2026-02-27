package com.comedorucv.vistas;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class LogoPlaceholder extends JPanel {
    private final int size = 65;
    private Image logo;

    public LogoPlaceholder() {
        setPreferredSize(new Dimension(size + 15, size + 15));
        setOpaque(false);
        try {
            java.net.URL res = getClass().getResource("/imagenes/Logo_Universidad_Central_de_Venezuela.png");
            if (res != null) {
                javax.swing.ImageIcon icon = new javax.swing.ImageIcon(res);
                if (icon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
                    logo = icon.getImage();
                }
            }
        } catch (Exception e) {
        }
    }

    public LogoPlaceholder(int size) {
        this(); // Ignore passed size, force 65 as requested by user
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (logo != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            double scale = Math.min((double) size / logo.getWidth(null),
                    (double) size / logo.getHeight(null));
            int w = (int) (logo.getWidth(null) * scale);
            int h = (int) (logo.getHeight(null) * scale);

            // Center horizontally within preferred width mostly
            int x = (getWidth() - w) / 2;
            int y = (getHeight() - h) / 2;

            g2.drawImage(logo, x, y, w, h, null);
            g2.dispose();
        }
    }
}
