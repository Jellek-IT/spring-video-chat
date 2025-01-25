package pl.bronikowski.springchat.backendmain.config.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.sql.DataSource;
import java.time.Clock;

@Configuration
@EnableScheduling
@ConditionalOnProperty(value = "app.scheduling.enabled", havingValue = "true")
// scheduled tasks blocked at least for 5 minutes for clusters
@EnableSchedulerLock(defaultLockAtMostFor = "10m", defaultLockAtLeastFor = "5m")
@RequiredArgsConstructor
@Slf4j
public class SchedulingConfig {
    private final SchedulingProperties properties;
    private final Clock clock;

    @Bean
    public TaskScheduler taskScheduler() {
        var taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setErrorHandler(t -> log.error("Scheduled task error", t));
        taskScheduler.setPoolSize(properties.threadPoolSize());
        taskScheduler.setClock(clock);
        return taskScheduler;
    }

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        .usingDbTime()
                        .build());
    }
}
