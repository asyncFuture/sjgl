package me.async.test;

import me.async.sjgl.FrameBuffer;
import me.async.sjgl.SJGL;
import me.async.sjgl.Shader;
import me.async.sjgl.Texture;
import me.async.sjgl.math.Vector2f;
import me.async.sjgl.math.Vector4f;
import me.async.sjgl.system.Display;

public class Test {

    public static Shader PROGRAM_SHADER = new Shader() {

        @Layout(index = 0)
        private Vector4f inVertex;

        @Layout(index = 1)
        private Vector2f inUv;

        @Interpolate
        private Vector2f uv;

        @Uniform(key = "sample01")
        private Texture texture;

        @Override
        public Vector4f vertex() {
            uv = inUv;
            return inVertex;
        }

        @Override
        public Vector4f fragment() {
            return texture.texture(uv);
        }
    };

    public static void main(String[] args) {
        Display display = new Display("First Triangle", 800, 800);
        FrameBuffer buffer = display.frameBuffer();
        SJGL sjgl = new SJGL(buffer);

        PROGRAM_SHADER.buffer().store(0, new Vector4f[]{
                new Vector4f(-0.5f, 0.5f, 0, 1),
                new Vector4f(-0.5f, -0.5f, 0, 1),
                new Vector4f(0.5f, -0.5f, 0, 1),

                new Vector4f(-0.5f, 0.5f, 0, 1),
                new Vector4f(0.5f, 0.5f, 0, 1),
                new Vector4f(0.5f, -0.5f, 0, 1),

        }).store(1, new Vector2f[]{
                new Vector2f(0, 0),
                new Vector2f(0, 1),
                new Vector2f(1, 1),

                new Vector2f(0, 0),
                new Vector2f(1, 0),
                new Vector2f(1, 1),
        });

        Texture texture = new Texture(Texture.class.getResourceAsStream("/grass.png"));

        sjgl.setShader(PROGRAM_SHADER);

        display.show();
        while (!display.isRequestClosing()) {
            buffer.clear(SJGL.rgba(0, 0, 0, 255));

            Shader.setUniform(PROGRAM_SHADER,"sample01", texture);
            sjgl.drawTriangles(6);

            display.swap();
            display.sleep(16);
        }
        display.destroy();
    }
}