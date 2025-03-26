package co.edu.unicauca.monitoring.tool.backend.notification.ms.consumer;

import co.edu.unicauca.monitoring.tool.backend.notification.ms.domain.HealthEndpointNotificationDto;

public interface IHealthEndpointConsumer {
    void read(final HealthEndpointNotificationDto payload);
}
