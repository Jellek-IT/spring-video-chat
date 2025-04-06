package pl.bronikowski.springchat.backendnotifications.config.async;

import lombok.RequiredArgsConstructor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pl.bronikowski.springchat.backendnotifications.config.Profiles;

import java.util.concurrent.Executor;

@Profile(Profiles.ASYNC)
@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AsyncConfig implements AsyncConfigurer {
    private final AsyncProperties asyncProperties;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(asyncProperties.corePoolSize());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AppUncaughtExceptionHandler();
    }
}
