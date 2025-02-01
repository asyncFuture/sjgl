package me.async.sjgl;

import me.async.sjgl.math.Barycentric;
import me.async.sjgl.math.Vector3f;
import me.async.sjgl.math.Vector4f;

import java.util.LinkedList;
import java.util.List;

public class SJGL {

    private final FrameBuffer buffer;
    private Shader shader;

    public SJGL(FrameBuffer buffer) {
        this.buffer = buffer;
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
    }

    public static Vector4f ndc(FrameBuffer buffer, Vector4f vec) {
        vec.x /= vec.w;
        vec.y /= vec.w;
        vec.z /= vec.w;
        vec.w /= vec.w;

        float x = Math.round((vec.x + 1.0f) * buffer.halfWidth());
        float y = Math.round((-vec.y + 1.0f) * buffer.halfHeight());

        return new Vector4f(x, y, vec.z, vec.w);
    }

    public void drawTriangles(int count) {
        Shader.Buffer shaderBuffer = shader.buffer();

        for (int i = 0; i < count; i += 3) {
            Vector4f[] vertices = new Vector4f[3];
            List<Object> interpolates = new LinkedList<>();
            for (int j = 0; j < 3; j++) {
                for (int i1 = 0; i1 < shaderBuffer.size(); i1++) {
                    Object[] objects = shaderBuffer.get(i1);
                    Shader.setLayout(shader, i1, objects[i + j]);
                }
                vertices[j] = ndc(buffer, shader.vertex());
                Shader.interpolates(shader, interpolates);
            }

            Vector4f v0 = vertices[0];
            Vector4f v1 = vertices[1];
            Vector4f v2 = vertices[2];

            int[] bounding = BoundingBox.bounding(v0, v1, v2);
            int minX = bounding[0];
            int maxX = bounding[1];

            int minY = bounding[2];
            int maxY = bounding[3];

            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    Vector3f baryCords = Barycentric.computeBarycentricCoords(v0, v1, v2, x, y);
                    float u = baryCords.x;
                    float v = baryCords.y;
                    float w = baryCords.z;

                    if ((w >= 0) && (v >= 0) && (w + u <= 1)) {
                        for (int i1 = 0; i1 < interpolates.size() / 3; i1++) {
                            Object f = Barycentric.interpolate(i1, interpolates, u, v, w);
                            Shader.setInterpolate(shader, f);
                        }
                        Vector4f fragment = shader.fragment();

                        buffer.setPixel(SJGL.rgba(fragment.x, fragment.y, fragment.z, fragment.w), x, y);
                    }
                }
            }
        }
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public FrameBuffer buffer() {
        return buffer;
    }
}