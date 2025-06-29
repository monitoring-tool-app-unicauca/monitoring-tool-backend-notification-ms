package co.edu.unicauca.monitoring.tool.backend.notification.ms.config;

import co.edu.unicauca.monitoring.tool.backend.notification.ms.handler.NotificationWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Configuration class for setting up WebSocket communication.
 * <p>
 * This class registers the {@link NotificationWebSocketHandler} to handle WebSocket
 * connections at the specified endpoint. It also allows all origins for cross-origin
 * requests.
 * </p>
 */
@Configuration
public class WebSocketConfig implements WebSocketConfigurer {

    private final NotificationWebSocketHandler handler;

    /**
     * Constructs a WebSocketConfig with the given NotificationWebSocketHandler.
     *
     * @param handler the WebSocket handler used to manage notification messages
     */
    public WebSocketConfig(NotificationWebSocketHandler handler) {
        this.handler = handler;
    }

    /**
     * Registers the WebSocket handler and maps it to the /ws/notifications endpoint.
     *
     * @param registry the WebSocketHandlerRegistry to register handlers with
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/notifications").setAllowedOrigins("*");
    }
}
