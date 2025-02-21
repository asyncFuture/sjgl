package me.async.sjgl.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SJGLUtils {

    public static BufferedImage converter(BufferedImage image) {
        BufferedImage intImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g = intImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return intImage;
    }
}