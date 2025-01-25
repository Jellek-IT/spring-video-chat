package pl.bronikowski.springchat.backendmain.videoroom.internal;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VideoRoomScheduler {
    private final VideoRoomService videoRoomService;

    @Scheduled(cron = "${app.scheduling.cron.video-room-clear-expired}")
    @SchedulerLock(name = "videoRoomScheduler")
    public void clearExpired() {
        videoRoomService.clearExpired();
    }
}
