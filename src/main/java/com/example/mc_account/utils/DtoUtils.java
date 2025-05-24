package com.example.mc_account.utils;

import lombok.experimental.UtilityClass;

import java.util.function.Consumer;
import java.util.function.Function;

@UtilityClass
public class DtoUtils {

    public static <T> void setIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    public static <T, R> void setIfNotNull(T value, Function<T, R> converter, Consumer<R> setter) {
        if (value != null) {
            setter.accept(converter.apply(value));
        }
    }
}
