package co.edu.unicauca.monitoring.tool.backend.notification.ms.handler;

import co.edu.unicauca.monitoring.tool.backend.notification.ms.domain.NotificationDto;
import co.edu.unicauca.monitoring.tool.backend.notification.ms.domain.SubscriptionMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationWebSocketHandler.class);
    public static final String SUBSCRIBE_WS = "SUBSCRIBE";
    private final Map<WebSocketSession, Boolean> sessionSubscribed = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final ObjectMapper objectMapper;

    // WebSocketSession -> set of subscribed projectIds
    private final Map<WebSocketSession, Set<String>> sessionProjects = new ConcurrentHashMap<>();

    public NotificationWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        sessionProjects.put(session, ConcurrentHashMap.newKeySet());
        sessionSubscribed.put(session, false);
        logger.info("Connection established: {}", session.getId());
        scheduler.schedule(() -> {
            if (Boolean.FALSE.equals(sessionSubscribed.get(session))) {
                try {
                    logger.warn("Session {} did not subscribe in time. Closing connection.", session.getId());
                    session.close(CloseStatus.POLICY_VIOLATION);
                } catch (IOException e) {
                    logger.error("Error closing session {}: {}", session.getId(), e.getMessage(), e);
                }
            }
        }, 5, TimeUnit.SECONDS);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        try {
            SubscriptionMessageDto dto = objectMapper.readValue(message.getPayload(), SubscriptionMessageDto.class);

            if (SUBSCRIBE_WS.equalsIgnoreCase(dto.getType())
                    && dto.getProjects() != null
                    && !dto.getProjects().isEmpty()) {
                Set<String> projects = sessionProjects.computeIfAbsent(session, k -> new HashSet<>());
                projects.clear();
                projects.addAll(dto.getProjects());
                sessionSubscribed.put(session, true);
                logger.info("Session {} subscribed to projects: {}", session.getId(), projects);
            }

        } catch (Exception e) {
            logger.error("Invalid message from session {}: {}", session.getId(), e.getMessage(), e);
        }
    }


    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        sessionProjects.remove(session);
        logger.info("Connection closed: {}", session.getId());
    }

    @Async
    public void sendToProject(NotificationDto notificationDto) {
        String targetProjectId = notificationDto.getProjectId();

        try {
            String message = objectMapper.writeValueAsString(notificationDto);
            for (Map.Entry<WebSocketSession, Set<String>> entry : sessionProjects.entrySet()) {
                WebSocketSession session = entry.getKey();
                Set<String> subscribedProjects = entry.getValue();

                if (session.isOpen() && subscribedProjects.contains(targetProjectId)) {
                    session.sendMessage(new TextMessage(message));
                }
            }
        } catch (IOException e) {
            logger.error("Error sending message to project {}: {}", targetProjectId, e.getMessage(), e);
        }
    }
}
