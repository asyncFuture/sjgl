package me.async.sjgl.math;

public class Matrix3x3f {

    private float[] matrix;

    public Matrix3x3f(float[] matrix) {
        this.matrix = matrix;
    }

    public Matrix3x3f() {
        this(new float[3 * 3]);
    }

    public Matrix3x3f identity() {
        matrix[0] = 1;
        matrix[1] = 0;
        matrix[2] = 0;

        matrix[3] = 0;
        matrix[4] = 1;
        matrix[5] = 0;

        matrix[6] = 0;
        matrix[7] = 0;
        matrix[8] = 1;
        return this;
    }

    public Matrix3x3f fast(Vector2f translate, float rotate, Vector2f scale) {
        float cos = (float) Math.cos(rotate);
        float sin = (float) Math.sin(rotate);

        this.matrix = new float[]{
                scale.x * cos, -scale.y * sin, translate.x,
                scale.x * sin, scale.y * cos, translate.y,
                0, 0, 1
        };
        return this;
    }

    public Vector3f transform(Vector2f vec) {
        float x = matrix[0] * vec.x + matrix[1] * vec.y + matrix[2];
        float y = matrix[3] * vec.x + matrix[4] * vec.y + matrix[5];
        float z = matrix[6] * vec.x + matrix[7] * vec.y + matrix[8];
        return new Vector3f(x, y, z);
    }
}