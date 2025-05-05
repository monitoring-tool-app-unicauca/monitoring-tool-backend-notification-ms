package co.edu.unicauca.monitoring.tool.backend.notification.ms.domain;

import co.edu.unicauca.monitoring.tool.backend.notification.ms.enums.HealthStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class HealthEndpointNotificationDto {
    private String projectId;
    private String projectName;
    private List<To> to;
    private HealthStatus status;
    private String message;
    private String url;
}
