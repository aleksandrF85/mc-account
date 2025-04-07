package com.example.mc_account.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.util.List;

@UtilityClass
public class BeanUtils {

    @SneakyThrows
    public void copyNonNullProperties(Object source, Object destination) {
        Class<?> clazz = source.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(source);
            // TODO проверить логику копирования полей boolean
            if (value != null &&
                    !value.toString().isEmpty() &&
                    !value.equals(List.of()) &&
                    !value.getClass().getSimpleName().equals("boolean")) {
                field.set(destination, value);
            }
        }

    }
}
