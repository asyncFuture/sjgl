# SJGL
SJGL is a software rasterization library for Java. It can rasterize triangles and interpolate attributes, such as vectors for the respective vertices. With the built-in shader system, you can use both vertex and fragment shaders. The main goal is to make it easy for beginners to dive into 3D rasterization.


## Important:
- This project is still in its early stages. Later on, a 3D implementation will be included, featuring Matrix4f, frustum clipping, and perspective correction for textures.

## First Triangle:
<img alt="triangle.png" height="600" src="https://asyncfuture.de/triangle.png?ex=679ee682&is=679d9502&hm=efb572d7a3e504be00cf32b0abb79b4231b5899bd70ee490609b493eaa1350c4&" title="example-triangle" width="600"/>

````java
public class Test {

    public static double NS = 1000000000.0 / 60.0;

    public static Shader PROGRAM_SHADER = new Shader() {

        @Layout(index = 0)
        private Vector4f inVertex;

        @Layout(index = 1)
        private Vector2f inUv;

        @Interpolate
        private Vector2f uv;

        @Uniform(key = "transformation")
        private Matrix3x3f matrix;

        @Uniform(key = "sample01")
        private Texture texture;

        @Override
        public Vector4f vertex() {
            uv = inUv;
            Vector3f transform = matrix.transform(new Vector2f(inVertex.x, inVertex.y));
            return new Vector4f(transform, 1.0f);
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

        //important for the shader cache handling!
        PROGRAM_SHADER.compile();

        Texture texture = new Texture(Texture.class.getResourceAsStream("/grass.png"));
        Matrix3x3f transformation = new Matrix3x3f();
        float rotate = 0;

        sjgl.setShader(PROGRAM_SHADER);

        display.show();
        while (!display.isRequestClosing()) {
            buffer.clear(0xff);

            transformation.fast(new Vector2f(0, 0), (float) Math.toRadians(rotate++), new Vector2f(1, 1));

            Shader.setUniform(PROGRAM_SHADER, "sample01", texture);
            Shader.setUniform(PROGRAM_SHADER, "transformation", transformation);
            sjgl.drawTriangles(6);

            display.swap();
            display.sleep(16);
        }
        display.destroy();
    }
}
````
## Setup for gradle
````gradle
repositories {
    mavenCentral()
    maven("https://asyncfuture.de/libs")
}

dependencies {
    implementation("me.async.sjgl:sjgl:1.0-SNAPSHOT")
}
````
