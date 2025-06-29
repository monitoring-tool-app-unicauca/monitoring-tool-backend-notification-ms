package co.edu.unicauca.monitoring.tool.backend.notification.ms.consumer;


import co.edu.unicauca.monitoring.tool.backend.notification.ms.domain.WelcomePasswordNotificationDto;

public interface IWelcomePasswordConsumer {
    void read(final WelcomePasswordNotificationDto payload);
}
