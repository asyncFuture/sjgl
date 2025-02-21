package me.async.sjgl.math;

public class Vector2f {

    public float x, y;

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f(float x) {
        this(x, x);
    }
}