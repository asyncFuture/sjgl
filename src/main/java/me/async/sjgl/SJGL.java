package me.async.sjgl;

import me.async.sjgl.buffer.DepthBuffer;
import me.async.sjgl.buffer.FrameBuffer;
import me.async.sjgl.math.Vector4f;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SJGL {

    private final FrameBuffer buffer;
    private final DepthBuffer depthBuffer;

    private final Scanline scanline;

    private Shader shader;

    public SJGL(FrameBuffer buffer) {
        this.buffer = buffer;
        this.depthBuffer = new DepthBuffer(buffer.width(), buffer.height());
        this.scanline = new Scanline(buffer, depthBuffer);
    }

    public static int rgba(int red, int green, int blue, int alpha) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static int rgba(float red, float green, float blue, float alpha) {
        return rgba((int) (red * 255), (int) (green * 255), (int) (blue * 255), (int) (alpha * 255));
    }

    public static Vector4f rgba(int data) {
        return new Vector4f(getRed(data) / 255f, getGreen(data) / 255f, getBlue(data) / 255f, getAlpha(data) / 255f);
    }

    public static int getAlpha(int rgba) {
        return (rgba >> 24) & 0xFF;
    }

    public static int getRed(int rgba) {
        return (rgba >> 16) & 0xFF;
    }

    public static int getGreen(int rgba) {
        return (rgba >> 8) & 0xFF;
    }

    public static int getBlue(int rgba) {
        return rgba & 0xFF;
    }

    public void clear(int data) {
        buffer.clear(data);
        depthBuffer.clear();
    }

    public static Vector4f ndc(FrameBuffer buffer, Vector4f vec) {
        vec.x /= vec.w;
        vec.y /= vec.w;
        vec.z /= vec.w;

        float x = Math.round((vec.x + 1.0f) * buffer.halfWidth());
        float y = Math.round((-vec.y + 1.0f) * buffer.halfHeight());

        return new Vector4f(x, y, vec.z, vec.w);
    }

    public void drawTriangles(int count) {
        if (count % 3 != 0) {
            throw new RuntimeException("Invalid number of triangles: " + count + ". The count must be divisible by 3.");
        }

        Shader.Buffer shaderBuffer = shader.buffer();

        for (int i = 0; i < count; i += 3) {
            Vector4f[] vertices = new Vector4f[3];
            Map<String, List<Object>> interpolates = new HashMap<>();
            for (int j = 0; j < 3; j++) {
                for (int i1 = 0; i1 < shaderBuffer.size(); i1++) {
                    Object[] objects = shaderBuffer.getArray(i1);
                    Shader.setLayout(shader, i1, objects[i + j]);
                }
                vertices[j] = ndc(buffer, shader.vertex());
                Shader.interpolates(shader, interpolates);
            }

            Vector4f v0 = vertices[0];
            Vector4f v1 = vertices[1];
            Vector4f v2 = vertices[2];

            scanline.setVertices(new Vector4f[]{v0, v1, v2});
            scanline.setObjects(interpolates);

            scanline.rasterization();
        }
    }

    public void drawTrianglesIndices() {
        Shader.Buffer shaderBuffer = shader.buffer();
        int[] indices = shaderBuffer.getInts(0);

        if (indices.length % 3 != 0) {
            throw new RuntimeException("Invalid number of triangles: " + indices.length + ". The count must be divisible by 3.");
        }
        for (int i = 0; i < indices.length; i += 3) {
            Vector4f[] vertices = new Vector4f[3];
            Map<String, List<Object>> interpolates = new HashMap<>();

            for (int j = 0; j < 3; j++) {
                for (int i1 = 1; i1 < shaderBuffer.size(); i1++) {
                    Object[] array = shaderBuffer.getArray(i1);
                    Shader.setLayout(shader, i1, array[indices[i + j]]);
                }
                vertices[j] = ndc(buffer, shader.vertex());
                Shader.interpolates(shader, interpolates);
            }

            Vector4f v0 = vertices[0];
            Vector4f v1 = vertices[1];
            Vector4f v2 = vertices[2];

            scanline.setVertices(new Vector4f[]{v0, v1, v2});
            scanline.setObjects(interpolates);

            scanline.rasterization();
        }
    }

    public void setShader(Shader shader) {
        this.shader = shader;
        this.scanline.setShader(shader);
    }

    public FrameBuffer buffer() {
        return buffer;
    }
}