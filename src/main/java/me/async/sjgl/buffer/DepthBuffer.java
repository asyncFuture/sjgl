package me.async.sjgl.buffer;

import java.util.Arrays;

public class DepthBuffer {

    private int width, height;
    private float[][] data;

    public DepthBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.data = new float[width][height];
    }

    public void clear() {
        for (float[] floats : data) {
            Arrays.fill(floats, Float.POSITIVE_INFINITY);
        }
    }

    public boolean inDepthField(int x, int y) {
        return (x >= 0 && x < width && y >= 0 && y < height);
    }

    public void setDepth(int x, int y, float depth) {
        if (!inDepthField(x, y)) return;
        data[x][y] = depth;
    }

    public float getDepth(int x, int y) {
        if (!inDepthField(x, y)) return 0;
        return data[x][y];
    }
}