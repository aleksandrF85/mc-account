package com.example.mc_account.utils;

import lombok.experimental.UtilityClass;

import java.util.function.Consumer;

@UtilityClass
public class DtoUtils {

    public static <T> void setIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

}
