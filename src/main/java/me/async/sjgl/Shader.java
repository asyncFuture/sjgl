package me.async.sjgl;

import me.async.sjgl.math.Vector4f;
import me.async.sjgl.utils.BiAction;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class Shader {

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Layout {
        int index();
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Uniform {
        String key();
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Interpolate {
    }

    public static class Buffer {

        private final Map<Integer, Object[]> map = new HashMap<>();

        public Buffer store(int index, Object[] value) {
            map.put(index, value);
            return this;
        }

        public Object[] get(int index) {
            return map.get(index);
        }

        public int size() {
            return map.size();
        }

        public Map<Integer, Object[]> getMap() {
            return map;
        }
    }

    public static <T extends Annotation> void fields(Shader shader, Class<T> clazz, BiAction<Field, T> action) {
        for (Field field : shader.getClass().getDeclaredFields()) {
            try {
                field.trySetAccessible();
                T annotation = field.getAnnotation(clazz);
                if (annotation == null) continue;
                action.accept(field, annotation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void interpolates(Shader shader, Map<String, List<Object>> interpolates) {
        fields(shader, Interpolate.class, (field, interpolate) -> {
            try {
                Object e = field.get(shader);
                if (!interpolates.containsKey(field.getName())) {
                    interpolates.put(field.getName(), new LinkedList<>());
                }
                List<Object> objects = interpolates.get(field.getName());
                objects.add(e);

                interpolates.put(field.getName(), objects);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public static void setInterpolate(Shader shader, String key, Object f) {
        try {
            Field field = shader.interpolations.get(key);
            if (field == null) return;
            field.set(shader, f);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setUniform(Shader shader, String name, Object value) {
        try {
            Field field = shader.getUniforms().get(name);
            if (field == null) return;
            field.set(shader, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setLayout(Shader shader, int index, Object value) {
        try {
            Field field = shader.getLayouts().get(index);
            if (field == null) return;
            field.set(shader, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private final Map<String, Field> uniforms = new HashMap<>();
    private final Map<Integer, Field> layouts = new HashMap<>();
    private final Map<String, Field> interpolations = new HashMap<>();

    private final Buffer buffer = new Buffer();

    public abstract Vector4f vertex();

    public abstract Vector4f fragment();

    public Buffer buffer() {
        return buffer;
    }

    public void compile() {
        for (Field field : getClass().getDeclaredFields()) {
            field.trySetAccessible();

            Annotation uniform = field.getAnnotation(Uniform.class);
            if (uniform != null) {
                uniforms.put(((Uniform) uniform).key(), field);
            }

            uniform = field.getAnnotation(Layout.class);
            if (uniform != null) {
                layouts.put(((Layout) uniform).index(), field);
            }

            uniform = field.getAnnotation(Interpolate.class);
            if (uniform != null) {
                interpolations.put(field.getName(), field);
            }
        }
    }

    public Map<String, Field> getUniforms() {
        return uniforms;
    }

    public Map<Integer, Field> getLayouts() {
        return layouts;
    }

    public Map<String, Field> getInterpolations() {
        return interpolations;
    }
}