package pl.bronikowski.springchat.backendmain.shared.utils;

import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintViolation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {
    public static String camelCaseToEnum(String camelCase) {
        final String regex = "([a-z])([A-Z])";
        final String replacement = "$1_$2";

        return camelCase.replaceAll(regex, replacement).toUpperCase();
    }

    public static String toString(ConstraintViolation<?> violations) {
        return StringUtils.camelCaseToEnum(violations.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .getSimpleName());
    }

    public static boolean isEmpty(@Nullable String text) {
        return text == null || text.isEmpty();
    }
}
