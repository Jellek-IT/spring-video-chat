package pl.bronikowski.springchat.backendmain.config.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;

@Slf4j
public class AppUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {
    @Override
    public void handleUncaughtException(Throwable ex, Method method, @NonNull Object... params) {
        log.error("method {} thrown an exception {}", method.getName(), ex.getMessage());
    }
}
