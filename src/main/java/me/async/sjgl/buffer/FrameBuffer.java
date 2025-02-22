package me.async.sjgl.buffer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public class FrameBuffer {

    private final BufferedImage buffer;
    private int[] data;

    private int width, height;
    private int halfWidth, halfHeight;

    public FrameBuffer(int width, int height) {
        this.buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.data = ((DataBufferInt) buffer.getRaster().getDataBuffer()).getData();

        this.width = width;
        this.height = height;

        this.halfWidth = width / 2;
        this.halfHeight = height / 2;
    }

    public void setPixel(int color, int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            data[x + y * buffer.getWidth()] = color;
        }
    }

    public void clear(int color) {
        Arrays.fill(data, color);
    }

    public BufferedImage buffer() {
        return buffer;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int halfWidth() {
        return halfWidth;
    }

    public int halfHeight() {
        return halfHeight;
    }
}