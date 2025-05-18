package co.edu.unicauca.monitoring.tool.backend.notification.ms.consumer;

import co.edu.unicauca.monitoring.tool.backend.notification.ms.config.RabbitConfig;
import co.edu.unicauca.monitoring.tool.backend.notification.ms.domain.WelcomePasswordNotificationDto;
import co.edu.unicauca.monitoring.tool.backend.notification.ms.service.INotificationSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RabbitWelcomePasswordConsumerImpl implements IWelcomePasswordConsumer {

    private final INotificationSenderService notificationSenderService;

    @RabbitListener(queues = RabbitConfig.WELCOME_PASSWORD_QUEUE)
    @Override
    public void read(WelcomePasswordNotificationDto payload) {
        this.notificationSenderService.sendNotification(payload, WelcomePasswordNotificationDto.class);
    }
}
