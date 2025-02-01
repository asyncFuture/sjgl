package me.async.sjgl;

import me.async.sjgl.utils.BiAction;
import me.async.sjgl.math.Vector4f;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.HashMap;
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

    public static void setLayout(Shader shader, int index, Object object) {
        fields(shader, Layout.class, (field, layout) -> {
            if (layout.index() != index) return;
            field.set(shader, object);
        });
    }

    public static void setUniform(Shader shader, String key, Object object) {
        fields(shader, Uniform.class, (field, uniform) -> {
            if (!uniform.key().equals(key)) return;
            field.set(shader, object);
        });
    }

    public static void setInterpolate(Shader shader, Object object) {
        fields(shader, Interpolate.class, (field, interpolate) -> {
            if (field.getType() != object.getClass()) return;
            field.set(shader, object);
        });
    }

    public static void interpolates(Shader shader, List<Object> interpolates) {
        fields(shader, Interpolate.class, (field, interpolate) -> {
            try {
                Object e = field.get(shader);
                interpolates.add(e);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private final Buffer buffer = new Buffer();

    public abstract Vector4f vertex();

    public abstract Vector4f fragment();

    public Buffer buffer() {
        return buffer;
    }
}