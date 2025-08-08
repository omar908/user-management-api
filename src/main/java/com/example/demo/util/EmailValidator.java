package com.example.demo.util;

import java.util.regex.Pattern;

public class EmailValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE
    );

    public boolean isValid(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }
}


