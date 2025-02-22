package me.async.sjgl;

import me.async.sjgl.buffer.DepthBuffer;
import me.async.sjgl.buffer.FrameBuffer;
import me.async.sjgl.math.Barycentric;
import me.async.sjgl.math.Vector3f;
import me.async.sjgl.math.Vector4f;

import java.util.List;
import java.util.Map;

public class Scanline {

    private final FrameBuffer buffer;
    private final DepthBuffer depthBuffer;
    private Shader shader;

    private Vector4f[] vertices;
    private Map<String, List<Object>> objects;

    public Scanline(FrameBuffer buffer, DepthBuffer depthBuffer) {
        this.buffer = buffer;
        this.depthBuffer = depthBuffer;
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
        vertices = null;
        objects = null;
    }

    public static void swap(Vector4f[] vertices, Map<String, List<Object>> objects, Vector4f v0, Vector4f v1, int index) {
        if (v0.y > v1.y) {
            vertices[index] = v1;
            vertices[index + 1] = v0;

            for (String s : objects.keySet()) {
                List<Object> list = objects.get(s);
                for (int i = 0; i < list.size() / 3; i += 2) {
                    int i0 = index + i;
                    int i1 = index + (i + 1);

                    Object temp = list.get(i0);
                    list.set(i0, list.get(i1));
                    list.set(i1, temp);

                    objects.put(s, list);
                }
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

            shader.sjgl_Pos = new Vector4f(
                    u * vertices[0].x + v * vertices[1].x + w * vertices[2].x,
                    u * vertices[0].y + v * vertices[1].y + w * vertices[2].y,
                    u * vertices[0].z + v * vertices[1].z + w * vertices[2].z,
                    u * vertices[0].w + v * vertices[1].w + w * vertices[2].w
            );

            float depth = 1.0f / (shader.sjgl_Pos.z / shader.sjgl_Pos.w);
            if (depthBuffer.getDepth(i, y) < depth) {
                continue;
            }

            depthBuffer.setDepth(i, y, depth);

            for (String s : objects.keySet()) {
                List<Object> list = objects.get(s);

                for (int i1 = 0; i1 < list.size() / 3; i1++) {
                    Object f = Barycentric.interpolate(i1, list, u, v, w);
                    Shader.setInterpolate(shader, s, f);
                }
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

    public void setObjects(Map<String, List<Object>> objects) {
        this.objects = objects;
    }
}