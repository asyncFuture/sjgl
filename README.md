# SJGL
SJGL is a software rasterization library for Java. It can rasterize triangles and interpolate attributes, such as vectors for the respective vertices. With the built-in shader system, you can use both vertex and fragment shaders. The main goal is to make it easy for beginners to dive into 3D rasterization.

# First Triangle
<img alt="triangle.png" height="600" src="https://cdn.discordapp.com/attachments/1171315508008714261/1335089519074086955/triangle.png?ex=679ee682&is=679d9502&hm=efb572d7a3e504be00cf32b0abb79b4231b5899bd70ee490609b493eaa1350c4&" title="example-triangle" width="600"/>

````java
public class Main {
    public static Shader PROGRAM_SHADER = new Shader() {

        @Layout(index = 0)
        private Vector4f inVertex;

        @Layout(index = 1)
        private Vector4f inColor;

        @Interpolate
        private Vector4f color;

        @Override
        public Vector4f vertex() {
            color = inColor;
            return inVertex;
        }

        @Override
        public Vector4f fragment() {
            return color;
        }
    };

    public static void main(String[] args) {
        Display display = new Display("First Triangle", 600, 600);
        FrameBuffer buffer = display.frameBuffer();
        SJGL sjgl = new SJGL(buffer);

        PROGRAM_SHADER.buffer().store(0, new Vector4f[]{
                new Vector4f(0, 0.5f, 0, 1),
                new Vector4f(-0.5f, -0.5f, 0, 1),
                new Vector4f(0.5f, -0.5f, 0, 1),

        }).store(1, new Vector4f[]{
                new Vector4f(1, 0, 0, 1),
                new Vector4f(0, 1, 0, 1),
                new Vector4f(0, 0, 1, 1),
        });

        sjgl.setShader(PROGRAM_SHADER);

        display.show();
        while (!display.isRequestClosing()) {
            buffer.clear(0xff);

            sjgl.drawTriangles(3);

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