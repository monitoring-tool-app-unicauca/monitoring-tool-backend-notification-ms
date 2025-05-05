package co.edu.unicauca.monitoring.tool.backend.notification.ms.consumer;

import co.edu.unicauca.monitoring.tool.backend.notification.ms.config.RabbitConfig;
import co.edu.unicauca.monitoring.tool.backend.notification.ms.domain.PasswordRecoveryDto;
import co.edu.unicauca.monitoring.tool.backend.notification.ms.service.INotificationSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RabbitPasswordRecoveryConsumerImpl implements IPasswordRecoveryConsumer {

    private final INotificationSenderService notificationService;

    @RabbitListener(queues = RabbitConfig.PASSWORD_RECOVERY_QUEUE)
    @Override
    public void read(PasswordRecoveryDto payload) {
        this.notificationService.sendNotification(payload, PasswordRecoveryDto.class);
    }

}
