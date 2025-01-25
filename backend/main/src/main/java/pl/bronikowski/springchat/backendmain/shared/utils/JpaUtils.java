package pl.bronikowski.springchat.backendmain.shared.utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.MessageFormat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JpaUtils {
    public static Predicate likeIgnoreCase(Expression<String> expression, String s, CriteriaBuilder cb) {
        return cb.like(cb.lower(expression), contains(s).toLowerCase(), '\\');
    }

    public static String contains(String expression) {
        return MessageFormat.format("%{0}%", escapeWildcardChars(expression));
    }

    private static String escapeWildcardChars(String text) {
        return text.replaceAll("([%_\\\\])", "\\\\$1");
    }
}
