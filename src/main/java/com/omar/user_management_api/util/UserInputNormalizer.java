package com.omar.user_management_api.util;

import java.util.function.Function;

public class UserInputNormalizer {
    public static Function<String, String> catchNullValuesForString = defaultIfNull("");
    public static Function<String, String> trimString = String::trim;
    public static Function<String, String> lowercaseString = String::toLowerCase;

    public static Function<String, String> normalizeEmail =  catchNullValuesForString.andThen(trimString).andThen(lowercaseString);
    public static Function<String, String> normalizeName = catchNullValuesForString.andThen(trimString);

    public static <T> Function <T, T> defaultIfNull(T fallback) {
        return input -> input == null ? fallback : input;
    }
}
