package co.edu.unicauca.monitoring.tool.backend.notification.ms.service;

public interface INotificationService {
    <T> void sendNotification(final T payload, Class<T> payloadClass);
}
