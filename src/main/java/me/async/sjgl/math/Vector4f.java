package me.async.sjgl.math;

public class Vector4f {

    public float x, y, z, w;

    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4f(Vector4f v) {
        this(v.x, v.y, v.z, v.w);
    }

    public Vector4f(Vector2f vec, float w) {
        this(vec.x, vec.y, 0, w);
    }

    public Vector4f(Vector3f v, float w) {
        this(v.x, v.y, v.z, w);
    }

    public Vector4f sub(Vector4f v) {
        return new Vector4f(x - v.x, y - v.y, z - v.z, w - v.w);
    }

    public Vector4f add(Vector4f v) {
        return new Vector4f(x + v.x, y + v.y, z + v.z, w + v.w);
    }

    public Vector4f mul(Vector4f v) {
        return new Vector4f(x * v.x, y * v.y, z * v.z, w * v.w);
    }

    public Vector4f mul(float v) {
        return new Vector4f(x * v, y * v, z * v, w * v);
    }

    public float dot(Vector4f v) {
        return this.x * v.x + this.y * v.y + this.z * v.z + this.w * v.w;
    }

    @Override
    public String toString() {
        return "Vector4f{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", w=" + w +
                '}';
    }
}