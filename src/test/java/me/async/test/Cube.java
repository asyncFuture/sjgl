package me.async.test;

import me.async.sjgl.SJGL;
import me.async.sjgl.Shader;
import me.async.sjgl.math.Matrix4f;
import me.async.sjgl.math.Vector3f;
import me.async.sjgl.math.Vector4f;
import me.async.sjgl.system.Display;
import me.async.sjgl.system.Input;

import java.awt.event.MouseEvent;

public class Cube {

    public static final Shader SHADER = new Shader() {

        @Layout(index = 1)
        private Vector3f inVert;

        @Layout(index = 2)
        private Vector4f inColor;

        @Interpolate
        private Vector4f color;

        @Uniform(key = "matrix")
        private Matrix4f matrix;

        @Override
        public Vector4f vertex() {
            color = inColor;
            return matrix.transform(new Vector4f(inVert, 1.0f));
        }

        @Override
        public Vector4f fragment() {
            Vector4f output = color;
          //  output.w = sjgl_Pos.z / sjgl_Pos.w;
            return output;
        }
    };

    public static void main(String[] args) {
        Display display = new Display("Projection Test", 500, 500);
        SJGL sjgl = new SJGL(display.frameBuffer());

        SHADER.buffer().store(0, new int[]{
                0, 1, 2,
                0, 3, 2,

                4, 5, 6,
                4, 7, 6,

                8, 9, 10,
                8, 11, 10,

                12, 13, 14,
                12, 15, 14,
        }).store(1, new Vector3f[]{
                new Vector3f(-0.5f, 0.5f, -0.5f),
                new Vector3f(-0.5f, -0.5f, -0.5f),
                new Vector3f(0.5f, -0.5f, -0.5f),
                new Vector3f(0.5f, 0.5f, -0.5f),

                new Vector3f(-0.5f, 0.5f, 0.5f),
                new Vector3f(-0.5f, -0.5f, 0.5f),
                new Vector3f(0.5f, -0.5f, 0.5f),
                new Vector3f(0.5f, 0.5f, 0.5f),

                new Vector3f(-0.5f, 0.5f, -0.5f),
                new Vector3f(-0.5f, 0.5f, 0.5f),
                new Vector3f(-0.5f, -0.5f, 0.5f),
                new Vector3f(-0.5f, -0.5f, -0.5f),

                new Vector3f(0.5f, 0.5f, -0.5f),
                new Vector3f(0.5f, 0.5f, 0.5f),
                new Vector3f(0.5f, -0.5f, 0.5f),
                new Vector3f(0.5f, -0.5f, -0.5f),
        }).store(2, new Vector4f[]{
                new Vector4f(1, 0, 0, 1),
                new Vector4f(1, 0, 0, 1),
                new Vector4f(1, 0, 0, 1),
                new Vector4f(1, 0, 0, 1),

                new Vector4f(0, 1, 0, 1),
                new Vector4f(0, 1, 0, 1),
                new Vector4f(0, 1, 0, 1),
                new Vector4f(0, 1, 0, 1),

                new Vector4f(0, 0, 1, 1),
                new Vector4f(0, 0, 1, 1),
                new Vector4f(0, 0, 1, 1),
                new Vector4f(0, 0, 1, 1),

                new Vector4f(0, 1, 1, 1),
                new Vector4f(0, 1, 1, 1),
                new Vector4f(0, 1, 1, 1),
                new Vector4f(0, 1, 1, 1),
        });

        sjgl.setShader(SHADER.compile());

        Matrix4f perspective = new Matrix4f().identity().perspective((float) Math.toRadians(55), 1, 0.1f, 100f);

        Vector3f position = new Vector3f(0, 0, -5);
        Vector3f rotation = new Vector3f(45,45,0);


        display.show();
        while (!display.isRequestClosing()) {
            sjgl.clear(SJGL.rgba(0, 65, 190, 255));

            position.z -= Input.wheel() * 0.5f;

            if (Input.isButton(MouseEvent.BUTTON1)) {
                rotation.x += Input.mouseY();
                rotation.y += Input.mouseX();
            }

            Matrix4f matrix = new Matrix4f().identity().translate(position).rotate(rotation.toRadians()).scale(1);
            Shader.setUniform(SHADER, "matrix", new Matrix4f(perspective).mul(matrix));
            sjgl.drawTrianglesIndices();

            Input.update();
            display.swap();
            display.sleep(16);
        }
        display.destroy();
    }
}