package me.async.sjgl;

import me.async.sjgl.math.Vector2f;
import me.async.sjgl.math.Vector4f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.InputStream;

public class Texture {

    private BufferedImage buffer;
    private int[] data;

    private int width, height;

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

    public Texture(InputStream input) {
        try {
            this.buffer = ImageIO.read(input);
            this.buffer = converter(buffer);
            this.data = ((DataBufferInt) buffer.getRaster().getDataBuffer()).getData();
            this.width = buffer.getWidth();
            this.height = buffer.getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Vector4f texture(Vector2f uv) {
        int u = (int) (uv.x * (width - 1));
        int v = (int) (uv.y * (height - 1));
        return SJGL.rgba(getPixel(u, v));
    }

    public int getPixel(int x, int y) {
        return data[x + y * width];
    }
}