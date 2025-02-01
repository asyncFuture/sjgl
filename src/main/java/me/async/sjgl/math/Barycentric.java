package me.async.sjgl.math;

import java.util.List;

public class Barycentric {

    public static Vector3f computeBarycentricCoords(Vector4f v0, Vector4f v1, Vector4f v2, int x, int y) {
        Vector4f d0 = new Vector4f(v1).sub(v0);
        Vector4f d1 = new Vector4f(v2).sub(v0);
        Vector4f d2 = new Vector4f(x, y, 0, 0).sub(v0);

        float d00 = d0.dot(d0);
        float d01 = d0.dot(d1);
        float d11 = d1.dot(d1);
        float d20 = d2.dot(d0);
        float d21 = d2.dot(d1);
        float denom = d00 * d11 - d01 * d01;

        float v = (d11 * d20 - d01 * d21) / denom;
        float w = (d00 * d21 - d01 * d20) / denom;
        float u = 1.0f - v - w;

        return new Vector3f(u, v, w);
    }

    public static Object interpolate(int index, List<Object> objects, float u, float v, float w) {
        Object o0 = objects.get(index);
        Object o1 = objects.get(1 + index);
        Object o2 = objects.get(2 + index);

        if (o0 instanceof Vector4f v0 && o1 instanceof Vector4f v1 && o2 instanceof Vector4f v2) {
            return new Vector4f(
                    u * v0.x + v * v1.x + w * v2.x,
                    u * v0.y + v * v1.y + w * v2.y,
                    u * v0.z + v * v1.z + w * v2.z,
                    u * v0.w + v * v1.w + w * v2.w
            );
        } else if (o0 instanceof Vector3f v0 && o1 instanceof Vector3f v1 && o2 instanceof Vector3f v2) {
            return new Vector3f(
                    u * v0.x + v * v1.x + w * v2.x,
                    u * v0.y + v * v1.y + w * v2.y,
                    u * v0.z + v * v1.z + w * v2.z
            );
        } else if (o0 instanceof Vector2f v0 && o1 instanceof Vector2f v1 && o2 instanceof Vector2f v2) {
            return new Vector2f(
                    u * v0.x + v * v1.x + w * v2.x,
                    u * v0.y + v * v1.y + w * v2.y
            );
        }
        return null;
    }

}