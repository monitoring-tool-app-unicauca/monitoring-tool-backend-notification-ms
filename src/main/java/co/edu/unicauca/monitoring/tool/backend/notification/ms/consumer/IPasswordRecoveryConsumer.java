package co.edu.unicauca.monitoring.tool.backend.notification.ms.consumer;

import co.edu.unicauca.monitoring.tool.backend.notification.ms.domain.PasswordRecoveryDto;

public interface IPasswordRecoveryConsumer {
    void read(final PasswordRecoveryDto payload);
}
