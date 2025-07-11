package co.edu.unicauca.monitoring.tool.backend.notification.ms.consumer;

import co.edu.unicauca.monitoring.tool.backend.notification.ms.config.RabbitConfig;
import co.edu.unicauca.monitoring.tool.backend.notification.ms.domain.HealthEndpointNotificationDto;
import co.edu.unicauca.monitoring.tool.backend.notification.ms.domain.NotificationDto;
import co.edu.unicauca.monitoring.tool.backend.notification.ms.handler.NotificationWebSocketHandler;
import co.edu.unicauca.monitoring.tool.backend.notification.ms.service.INotificationSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RabbitHealthEndpointConsumerImpl implements IHealthEndpointConsumer {

    private final INotificationSenderService notificationSenderService;
    private final NotificationWebSocketHandler handler;

    @RabbitListener(queues = RabbitConfig.HEALTH_ENDPOINT_DOWN_QUEUE)
    @Override
    public void read(HealthEndpointNotificationDto payload) {
        var notificationWebSocketPayload = NotificationDto.builder()
                .projectId(payload.getProjectId())
                .messageNotification(payload.getMessage()).build();
        handler.sendToProject(notificationWebSocketPayload);
        this.notificationSenderService.sendNotification(payload, HealthEndpointNotificationDto.class);
    }

}
