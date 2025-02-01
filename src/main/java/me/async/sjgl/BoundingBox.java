package me.async.sjgl;

import me.async.sjgl.math.Vector4f;

public class BoundingBox {

    public static int[] bounding(Vector4f v0, Vector4f v1, Vector4f v2) {
        return new int[]{
                min(v0.x, v1.x, v2.x),
                max(v0.x, v1.x, v2.x),

                min(v0.y, v1.y, v2.y),
                max(v0.y, v1.y, v2.y),
        };
    }

    public static int min(float v0, float v1, float v2) {
        return (int) Math.min(v0, Math.min(v1, v2));
    }

    public static int max(float v0, float v1, float v2) {
        return (int) Math.max(v0, Math.max(v1, v2));
    }
}