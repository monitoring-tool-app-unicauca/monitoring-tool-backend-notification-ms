package co.edu.unicauca.monitoring.tool.backend.notification.ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableWebSocket
public class MonitoringToolBackendNotificationMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonitoringToolBackendNotificationMsApplication.class, args);
	}

}
