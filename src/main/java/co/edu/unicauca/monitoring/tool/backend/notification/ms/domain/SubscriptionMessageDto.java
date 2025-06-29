package co.edu.unicauca.monitoring.tool.backend.notification.ms.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubscriptionMessageDto {
    private String type;
    private List<String> projects;
}
