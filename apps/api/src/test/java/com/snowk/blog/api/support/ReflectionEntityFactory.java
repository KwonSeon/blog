package com.snowk.blog.api.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public final class ReflectionEntityFactory {

    private ReflectionEntityFactory() {
    }

    public static <T> T instantiate(Class<T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate: " + type.getName(), e);
        }
    }

    public static void setField(Object target, String fieldName, Object value) {
        Field field = findField(target.getClass(), fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Field not found: " + fieldName);
        }

        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to set field: " + fieldName, e);
        }
    }

    private static Field findField(Class<?> type, String fieldName) {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}
