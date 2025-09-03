package com.omar.user_management_api.util;

import java.util.function.Function;

public class UserInputNormalizer {
    public static final Function<String, String> catchNullValuesForString = defaultIfNull("");
    public static final Function<String, String> trimString = String::trim;
    public static final Function<String, String> lowercaseString = String::toLowerCase;

    public static final Function<String, String> normalizeEmail =  catchNullValuesForString.andThen(trimString).andThen(lowercaseString);
    public static final Function<String, String> normalizeName = catchNullValuesForString.andThen(trimString);

    public static <T> Function <T, T> defaultIfNull(T fallback) {
        return input -> input == null ? fallback : input;
    }
}
