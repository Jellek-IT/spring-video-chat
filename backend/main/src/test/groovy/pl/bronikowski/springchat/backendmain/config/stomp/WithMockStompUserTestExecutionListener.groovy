package pl.bronikowski.springchat.backendmain.config.stomp

import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.test.context.TestContext
import org.springframework.test.context.support.AbstractTestExecutionListener
import pl.bronikowski.springchat.backendmain.websocket.internal.authentication.StompAuthenticationProvider

import java.lang.reflect.AnnotatedElement

class WithMockStompUserTestExecutionListener extends AbstractTestExecutionListener {
    @Override
    void beforeTestMethod(TestContext testContext) {
        var annotated = (AnnotatedElement) testContext.getTestMethod()
        var annotation = AnnotatedElementUtils.findMergedAnnotation(annotated, WithMockStompUser.class)
        if (annotation == null) {
            return
        }
        var authentication = new StompTestAuthentication(annotation.value(), annotation.roles())
        getAuthenticationProvider(testContext).principal = authentication
    }

    @Override
    void afterTestMethod(TestContext testContext) {
        getAuthenticationProvider(testContext).principal = null
    }

    private static StompTestAuthenticationProvider getAuthenticationProvider(TestContext testContext) {
        var applicationContext = testContext.getApplicationContext()
        var authenticationProvider = applicationContext.getBean(StompAuthenticationProvider.class)
        if (!(authenticationProvider instanceof StompTestAuthenticationProvider)) {
            throw new RuntimeException("StompTestAuthenticationProvider is not present")
        }
        return authenticationProvider
    }
}