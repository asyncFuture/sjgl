package me.async.sjgl;

import me.async.sjgl.math.Barycentric;
import me.async.sjgl.math.Vector3f;
import me.async.sjgl.math.Vector4f;

import java.util.List;

public class Scanline {

    public final FrameBuffer buffer;
    public Shader shader;

    private Vector4f[] vertices;
    private List<Object> objects;

    public Scanline(FrameBuffer buffer) {
        this.buffer = buffer;
    }

    public void rasterization() {
        swap(vertices, objects, vertices[0], vertices[1], 0);
        swap(vertices, objects, vertices[1], vertices[2], 1);
        swap(vertices, objects, vertices[0], vertices[1], 0);

        Vector4f v0 = vertices[0];
        Vector4f v1 = vertices[1];
        Vector4f v2 = vertices[2];

        if (v1.y == v2.y) {
            drawBottomFlatTriangle(v0, v1, v2);
        } else if (v0.y == v1.y) {
            drawTopFlatTriangle(v0, v1, v2);
        } else {
            float v = (v1.y - v0.y) / (v2.y - v0.y);
            float splitX = v0.x + v * (v2.x - v0.x);
            float splitZ = v0.z + v * (v2.z - v0.z);
            float splitW = v0.w + v * (v2.w - v0.w);

            Vector4f split = new Vector4f(splitX, v1.y, splitZ, splitW);
            drawBottomFlatTriangle(v0, v1, split);
            drawTopFlatTriangle(v1, split, v2);
        }
    }

    public static void swap(Vector4f[] vertices, List<Object> objects, Vector4f v0, Vector4f v1, int index) {
        if (v0.y > v1.y) {
            vertices[index] = v1;
            vertices[index + 1] = v0;

            for (int i = 0; i < objects.size() / 3; i += 2) {
                int i0 = index + i;
                int i1 = index + (i + 1);

                Object temp = objects.get(i0);
                objects.set(i0, objects.get(i1));
                objects.set(i1, temp);
            }
        }
    }

    public void drawTopFlatTriangle(Vector4f v0, Vector4f v1, Vector4f v2) {
        float invSlope1 = (v2.x - v0.x) / (v2.y - v0.y);
        float invSlope2 = (v2.x - v1.x) / (v2.y - v1.y);

        float curX1 = v2.x;
        float curX2 = v2.x;

        for (int y = (int) Math.ceil(v2.y); y >= (int) Math.ceil(v0.y); y--) {
            drawLine(buffer, (int) curX1, (int) curX2, y);
            curX1 -= invSlope1;
            curX2 -= invSlope2;
        }
    }

    public void drawBottomFlatTriangle(Vector4f v0, Vector4f v1, Vector4f v2) {
        float invSlope1 = (v1.x - v0.x) / (v1.y - v0.y);
        float invSlope2 = (v2.x - v0.x) / (v2.y - v0.y);

        float curX1 = v0.x;
        float curX2 = v0.x;

        for (int y = (int) Math.ceil(v0.y); y <= (int) Math.ceil(v1.y); y++) {
            drawLine(buffer, (int) curX1, (int) curX2, y);
            curX1 += invSlope1;
            curX2 += invSlope2;
        }
    }

    public void drawLine(FrameBuffer buffer, int start, int end, int y) {
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }

        for (int i = start; i < end; i++) {
            Vector3f baryCords = Barycentric.computeBarycentricCoords(vertices[0], vertices[1], vertices[2], i, y);
            float u = baryCords.x;
            float v = baryCords.y;
            float w = baryCords.z;

            for (int i1 = 0; i1 < objects.size() / 3; i1++) {
                Object f = Barycentric.interpolate(i1, objects, u, v, w);
                Shader.setInterpolate(shader, f);
            }
            Vector4f fragment = shader.fragment();

            buffer.setPixel(SJGL.rgba(fragment.x, fragment.y, fragment.z, fragment.w), i, y);
        }
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public void setVertices(Vector4f[] vertices) {
        this.vertices = vertices;
    }

    public void setObjects(List<Object> objects) {
        this.objects = objects;
    }
}