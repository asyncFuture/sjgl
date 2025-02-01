package me.async.sjgl.math;

public class Vector3f {

    public float x, y, z;

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(Vector3f v) {
        this(v.x, v.y, v.z);
    }

    public Vector3f sub(Vector3f v) {
        return new Vector3f(x - v.x, y - v.y, z - v.z);
    }

    public Vector3f add(Vector3f v) {
        return new Vector3f(x + v.x, y + v.y, z + v.z);
    }

    public Vector3f mul(Vector3f v) {
        return new Vector3f(x * v.x, y * v.y, z * v.z);
    }

    public Vector3f mul(float v) {
        return new Vector3f(x * v, y * v, z * v);
    }

    public float dot(Vector3f v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    @Override
    public String toString() {
        return "Vector4f{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}