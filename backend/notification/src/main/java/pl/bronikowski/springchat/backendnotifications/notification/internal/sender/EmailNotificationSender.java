package pl.bronikowski.springchat.backendnotifications.notification.internal.sender;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.bronikowski.springchat.backendnotifications.notification.internal.NotificationChannel;
import pl.bronikowski.springchat.backendnotifications.notification.internal.Notification;
import pl.bronikowski.springchat.backendnotifications.notification.internal.NotificationContentEvaluator;
import pl.bronikowski.springchat.backendnotifications.notification.internal.NotificationRepository;
import pl.bronikowski.springchat.backendnotifications.shared.constants.ProjectConstants;
import pl.bronikowski.springchat.backendnotifications.shared.properties.PlatformProperties;

import java.time.Clock;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailNotificationSender implements NotificationSender {
    private final JavaMailSender javaMailSender;
    private final NotificationRepository notificationRepository;
    private final NotificationContentEvaluator notificationContentEvaluator;
    private final PlatformProperties platformProperties;
    private final Clock clock;

    @Override
    @Async
    public CompletableFuture<Void> send(Notification notification) {
        log.trace("Sending email notification [id={}]", notification.getId());
        try {
            var mimeMessage = createMimeMessage(notification);
            javaMailSender.send(mimeMessage);
            notification.setStatusSent(clock);
        } catch (MailException | MessagingException e) {
            notification.setStatusError(clock, e);
        }
        notificationRepository.save(notification);
        return null;
    }

    private MimeMessage createMimeMessage(Notification notification) throws MessagingException {
        var email = notification.getUser().email();
        var title = notificationContentEvaluator.evaluateTitle(notification);
        var content = notificationContentEvaluator.evaluateNotificationEmail(notification);

        var mimeMessage = javaMailSender.createMimeMessage();
        var mimeMessageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                ProjectConstants.DEFAULT_ENCODING);
        mimeMessageHelper.setFrom(platformProperties.email());
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject(platformProperties.prefix() + " " + title);
        mimeMessageHelper.setText(content, true);
        return mimeMessage;
    }

    @Override
    public boolean isApplicable(NotificationChannel channel) {
        return channel == NotificationChannel.EMAIL;
    }
}
