package com.omar.user_management_api;

import com.omar.user_management_api.util.UserInputNormalizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class UserInputNormalizerTest {

    @DisplayName("Should normalize a name with leading/trailing spaces, while being case sensitive")
    @ParameterizedTest
    @MethodSource("nameProviderData")
    public void normalizeNameTest(String input, String expected){
        var normalizedName = UserInputNormalizer.normalizeName.apply(input);
        Assertions.assertEquals(expected, normalizedName);
    }

    private static Stream<Arguments> nameProviderData(){
        return Stream.of(
                Arguments.of("name", "name"),
                Arguments.of("Name  ", "Name"),
                Arguments.of("  NAME", "NAME"),
                Arguments.of("  NaMe  ", "NaMe"),
                Arguments.of(null, ""),
                Arguments.of("  FiRstName LaStName  ", "FiRstName LaStName"),
                Arguments.of("  FIRSTNAME LASTNAME", "FIRSTNAME LASTNAME"),
                Arguments.of("Firstname Lastname  ", "Firstname Lastname")
        );
    }

    @DisplayName("Should normalize an email with leading/trailing spaces and lowercasing email")
    @ParameterizedTest
    @MethodSource("emailProviderData")
    public void normalizeEmailTest(String input, String expected){
        var normalizedEmail = UserInputNormalizer.normalizeEmail.apply(input);
        Assertions.assertEquals(expected, normalizedEmail);
    }

    private static Stream<Arguments> emailProviderData(){
        return Stream.of(
                Arguments.of("test@email.com", "test@email.com"),
                Arguments.of("Test@email.com  ", "test@email.com"),
                Arguments.of("  TEST@email.com", "test@email.com"),
                Arguments.of("  TeSt@email.com  ", "test@email.com"),
                Arguments.of(null, "")
        );
    }
}
