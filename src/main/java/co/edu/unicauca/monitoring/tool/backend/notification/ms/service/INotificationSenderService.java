package co.edu.unicauca.monitoring.tool.backend.notification.ms.service;

public interface INotificationSenderService {
    <T> void sendNotification(final T payload, Class<T> payloadClass);
}
