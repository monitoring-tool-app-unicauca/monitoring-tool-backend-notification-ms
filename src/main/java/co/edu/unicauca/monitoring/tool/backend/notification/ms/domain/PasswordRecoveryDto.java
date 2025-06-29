package co.edu.unicauca.monitoring.tool.backend.notification.ms.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRecoveryDto {
    private String urlResetLink;
    private To recipient;
}
