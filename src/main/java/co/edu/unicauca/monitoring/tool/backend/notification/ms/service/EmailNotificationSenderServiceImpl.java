package co.edu.unicauca.monitoring.tool.backend.notification.ms.service;

import co.edu.unicauca.monitoring.tool.backend.notification.ms.config.MessageLoader;
import co.edu.unicauca.monitoring.tool.backend.notification.ms.domain.HealthEndpointNotificationDto;
import co.edu.unicauca.monitoring.tool.backend.notification.ms.domain.PasswordRecoveryDto;
import co.edu.unicauca.monitoring.tool.backend.notification.ms.domain.To;
import co.edu.unicauca.monitoring.tool.backend.notification.ms.util.MessagesConstants;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmailNotificationSenderServiceImpl implements INotificationSenderService {

    private final JavaMailSender emailSender;
    private final ResourceLoader resourceLoader;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);


    @Override
    public <T> void sendNotification(T payload, Class<T> payloadClass) {
        if (payloadClass.equals(HealthEndpointNotificationDto.class)) {
            HealthEndpointNotificationDto healthPayload = (HealthEndpointNotificationDto) payload;
            healthPayload.getTo().forEach(recipient -> executorService.submit(() ->
                    sendHealthEndpointEmail(healthPayload, recipient)));
        } else if (payloadClass.equals(PasswordRecoveryDto.class)) {
            PasswordRecoveryDto passwordRecoveryPayload = (PasswordRecoveryDto) payload;
            sendPasswordRecoveryEmail(passwordRecoveryPayload, passwordRecoveryPayload.getRecipient());
        }
    }

    /**
     * Sends an email notification to a specific recipient.
     *
     * @param payload   the notification payload containing the project and endpoint details
     * @param recipient the recipient of the email
     */
    private void sendPasswordRecoveryEmail(PasswordRecoveryDto payload, To recipient) {
        try {
            MimeMessage message = createMimeMessage(MessageLoader.getInstance().getMessage(MessagesConstants.IM002),
                    recipient.getEmail(),MessageLoader.getInstance().getMessage(MessagesConstants.IM003)
                    ,createPasswordRecoveryEmailBody(payload), null);
            emailSender.send(message);
        } catch (MessagingException e) {
            handleEmailException(e, recipient);
        }
    }

    /**
     * Sends an email notification to a specific recipient.
     *
     * @param payload   the notification payload containing the project and endpoint details
     * @param recipient the recipient of the email
     */
    private void sendHealthEndpointEmail(HealthEndpointNotificationDto payload, To recipient) {
        try {
            MimeMessage message = createMimeMessage(MessageLoader.getInstance().getMessage(MessagesConstants.IM002),
                    recipient.getEmail(),MessageLoader.getInstance().getMessage(MessagesConstants.IM001,
                            payload.getStatus()), createHealthEndpointEmailBody(payload, recipient),
                    "classpath:templates/image-notification.png");
            emailSender.send(message);
        } catch (MessagingException e) {
            handleEmailException(e, recipient);
        }
    }

    private MimeMessage createMimeMessage(String from, String to, String subject, String body, String pathImage)
            throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        if (pathImage != null) {
            Resource resource = resourceLoader.getResource(pathImage);
            helper.addInline("logoImage", resource);
        }
        return message;
    }

    /**
     * Creates the email body by reading the template from a file and replacing placeholders with actual values.
     *
     * @param payload   the notification payload containing the project and endpoint details
     * @return the email body as a String
     */
    private String createPasswordRecoveryEmailBody(PasswordRecoveryDto payload) {
        try {
            Resource resource = resourceLoader.getResource("classpath:templates/password-recovery.html");
            String template = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            return template.replace("{url}", payload.getUrlResetLink())
                    .replace("{user}", payload.getRecipient().getName());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read email template", e);
        }
    }

    /**
     * Creates the email body by reading the template from a file and replacing placeholders with actual values.
     *
     * @param payload   the notification payload containing the project and endpoint details
     * @param recipient the recipient of the email
     * @return the email body as a String
     */
    private String createHealthEndpointEmailBody(HealthEndpointNotificationDto payload, To recipient) {
        try {
            Resource resource = resourceLoader.getResource("classpath:templates/notification.html");
            String template = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            return template.replace("{project}", payload.getProjectName())
                    .replace("{endpoint}", payload.getUrl())
                    .replace("{user}", recipient.getName());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read email template", e);
        }
    }


    /**
     * Handles exceptions that occur during email sending.
     *
     * @param e         the MessagingException that occurred
     * @param recipient the recipient of the email
     */
    private void handleEmailException(MessagingException e, To recipient) {
        System.err.println("Failed to send email to " + recipient.getEmail() + ": " + e.getMessage());
    }

}
