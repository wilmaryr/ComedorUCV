package com.comedorucv.modelos;

import com.comedorucv.utils.DataPathResolver;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ReconocimientoFacial {

    public boolean validarIdentidad(String cedula, File imagenSubida) {
        String dataDir = DataPathResolver.getDataFolder();
        File fotoReferencia = new File(
                dataDir + File.separator + "fotos_secretaria" + File.separator + cedula + ".png");

        if (!fotoReferencia.exists()) {
            System.err.println("No se encontró foto de referencia en: " + fotoReferencia.getAbsolutePath());
            return false;
        }

        try {
            BufferedImage imgA = ImageIO.read(fotoReferencia);
            BufferedImage imgB = ImageIO.read(imagenSubida);
            return compararImagenes(imgA, imgB);
        } catch (IOException e) {
            System.err.println("Error al procesar las imágenes: " + e.getMessage());
            return false;
        }
    }

    private boolean compararImagenes(BufferedImage imgA, BufferedImage imgB) {
        if (imgA.getWidth() != imgB.getWidth() || imgA.getHeight() != imgB.getHeight()) {
            return false;
        }

        int width = imgA.getWidth();
        int height = imgA.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }
}
