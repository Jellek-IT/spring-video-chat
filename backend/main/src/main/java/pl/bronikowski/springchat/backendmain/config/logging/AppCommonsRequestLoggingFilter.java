package pl.bronikowski.springchat.backendmain.config.logging;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.Set;

public class AppCommonsRequestLoggingFilter extends CommonsRequestLoggingFilter {
    private static final Set<String> secretAttributeNames = Set.of("password");

    @Override
    protected void afterRequest(@NonNull HttpServletRequest request, @NonNull String message) {
        String processedMessage = hideSecrets(message);
        super.afterRequest(request, processedMessage);
    }

    private String hideSecrets(String message) {
        var attributesPattern = String.join("|", secretAttributeNames);
        var pattern = "\"((?:" + attributesPattern + ")\"\\s*:\\s*)\"(?:\\\\\"|[^\"])*+\"";
        return message.replaceAll(pattern, "$1\"*******\"");
    }
}
