# SJGL
SJGL is a software rasterization library for Java. It can rasterize triangles and interpolate attributes, such as vectors for the respective vertices. With the built-in shader system, you can use both vertex and fragment shaders. The main goal is to make it easy for beginners to dive into 3D rasterization.


## Important:
- This project is still in its early stages. Later on, a 3D implementation will be included, featuring Matrix4f, frustum clipping, and perspective correction for textures.

## First Triangle:
<img alt="triangle.png" height="600" src="https://asyncfuture.de/triangle.png?ex=679ee682&is=679d9502&hm=efb572d7a3e504be00cf32b0abb79b4231b5899bd70ee490609b493eaa1350c4&" title="example-triangle" width="600"/>

````java
public class Triangle {

    public static final Shader SHADER = new Shader() {

        @Layout(index = 0)
        private Vector2f inVert;

        @Layout(index = 1)
        private Vector4f inColor;

        @Interpolate
        private Vector4f color;

        @Override
        public Vector4f vertex() {
            color = inColor;
            return new Vector4f(inVert, 1);
        }

        @Override
        public Vector4f fragment() {
            return color;
        }
    };

    public static void main(String[] args) {
        Display display = new Display("Triangle", 600, 600);
        SJGL sjgl = new SJGL(display.frameBuffer());

        SHADER.buffer().store(0, new Vector2f[]{
                new Vector2f(0, 0.5f),
                new Vector2f(-0.5f, -0.5f),
                new Vector2f(0.5f, -0.5f),
        }).store(1, new Vector4f[]{
                new Vector4f(1,0,0,1),
                new Vector4f(0,1,0,1),
                new Vector4f(0,0,1,1),
        });

        display.show();
        sjgl.setShader(SHADER.compile());
        while (!display.isRequestClosing()) {
            sjgl.clear(0xff);


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
