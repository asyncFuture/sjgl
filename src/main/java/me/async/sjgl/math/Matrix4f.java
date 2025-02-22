package me.async.sjgl.math;


import java.util.Arrays;

public class Matrix4f {

    public float[] data;

    public Matrix4f() {
        this.data = new float[4 * 4];
    }

    public Matrix4f(float[] matrix) {
        this.data = matrix;
    }

    public Matrix4f(Matrix4f matrix) {
        this.data = matrix.data;
    }

    public static float[] mul(float[] m0, float[] m1) {
        float[] result = new float[16];

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                float sum = 0;
                for (int k = 0; k < 4; k++) {
                    sum += m0[row * 4 + k] * m1[k * 4 + col];
                }
                result[row * 4 + col] = sum;
            }
        }
        return result;
    }

    public static Matrix4f mul(Matrix4f m0, Matrix4f m1) {
        return new Matrix4f(mul(m0.data, m1.data));
    }

    public Matrix4f identity() {
        Arrays.fill(data, 0);
        data[0] = 1;
        data[5] = 1;
        data[10] = 1;
        data[15] = 1;
        return this;
    }

    public Matrix4f mul(Matrix4f target) {
        return mul(this, target);
    }

    public Matrix4f perspective(float fov, float aspect, float near, float far) {
        float tanHalfFov = (float) Math.tan(fov / 2.0);
        float range = near - far;
        float[] matrix = {
                1.0f / (aspect * tanHalfFov), 0, 0, 0,
                0, 1.0f / tanHalfFov, 0, 0,
                0, 0, (far + near) / range, 2 * far * near / range,
                0, 0, -1, 0
        };
        data = mul(data, matrix);
        return this;
    }

    public static Matrix4f transformation(Vector3f pos, Vector3f rot, Vector3f scale) {
        float[] matrix = {
                scale.x, 0, 0, pos.x,
                0, scale.y, 0, pos.y,
                0, 0, scale.z, pos.z,
                0, 0, 0, 1,
        };

        Matrix4f matrix4f = new Matrix4f(matrix);
        return matrix4f.rotate(rot.x, rot.y, rot.z);

    }


    public Matrix4f translate(float x, float y, float z) {
        float[] matrix = {
                1, 0, 0, x,
                0, 1, 0, y,
                0, 0, 1, z,
                0, 0, 0, 1,
        };
        data = mul(data, matrix);
        return this;
    }

    public Matrix4f translate(Vector3f position) {
        return translate(position.x, position.y, position.z);
    }

    public Matrix4f scale(float x, float y, float z) {
        float[] matrix = {
                x, 0, 0, 0,
                0, y, 0, 0,
                0, 0, z, 0,
                0, 0, 0, 1,
        };
        data = mul(data, matrix);
        return this;
    }

    public Matrix4f scale(Vector3f scale) {
        return scale(scale.x, scale.y, scale.z);
    }

    public Matrix4f scale(float scale) {
        return scale(scale, scale, scale);
    }

    public Matrix4f rotate(float x, float y, float z) {
        return rotateX(x).rotateY(y).rotateZ(z);
    }

    public Matrix4f rotate(Vector3f rotate) {
        return rotate(rotate.x, rotate.y, rotate.z);
    }

    private Matrix4f rotateX(float x) {
        float cos = (float) Math.cos(x);
        float sin = (float) Math.sin(x);
        float[] matrix = {
                1, 0, 0, 0,
                0, cos, -sin, 0,
                0, sin, cos, 0,
                0, 0, 0, 1,
        };
        data = mul(data, matrix);
        return this;
    }

    private Matrix4f rotateY(float y) {
        float cos = (float) Math.cos(y);
        float sin = (float) Math.sin(y);
        float[] matrix = {
                cos, 0, sin, 0,
                0, 1, 0, 0,
                -sin, 0, cos, 0,
                0, 0, 0, 1,
        };
        data = mul(data, matrix);
        return this;
    }

    private Matrix4f rotateZ(float z) {
        float cos = (float) Math.cos(z);
        float sin = (float) Math.sin(z);
        float[] matrix = {
                cos, -sin, 0, 0,
                sin, cos, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1,
        };
        data = mul(data, matrix);
        return this;
    }

    public Vector4f transform(Vector4f vec) {
        float x = data[0] * vec.x + data[1] * vec.y + data[2] * vec.z + data[3] * vec.w;
        float y = data[4] * vec.x + data[5] * vec.y + data[6] * vec.z + data[7] * vec.w;
        float z = data[8] * vec.x + data[9] * vec.y + data[10] * vec.z + data[11] * vec.w;
        float w = data[12] * vec.x + data[13] * vec.y + data[14] * vec.z + data[15] * vec.w;
        return new Vector4f(x, y, z, w);
    }

    @Override
    public String toString() {
        return "Matrix4f{" +
                "data=" + Arrays.toString(data) +
                '}';
    }
}