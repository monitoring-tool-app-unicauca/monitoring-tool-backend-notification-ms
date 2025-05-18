package co.edu.unicauca.monitoring.tool.backend.notification.ms.config;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

@Configuration
public class RabbitConfig {

    public static final String HEALTH_ENDPOINT_DOWN_QUEUE = "health.endpoint.down.queue";
    public static final String ROUTING_KEY_HEALTH_ENDPOINT_DOWN = "health.endpoint.down";
    public static final String HEALTH_ENDPOINT_EXCHANGE = "health.endpoint.exchange";

    public static final String PASSWORD_RECOVERY_QUEUE = "password.recovery.queue";
    public static final String ROUTING_KEY_PASSWORD_RECOVERY = "password.recovery";
    public static final String PASSWORD_RECOVERY_EXCHANGE = "password.recovery.exchange";


    public static final String WELCOME_PASSWORD_QUEUE = "welcome.password.queue";
    public static final String ROUTING_KEY_WELCOME_PASSWORD = "welcome.password";
    public static final String WELCOME_PASSWORD_EXCHANGE = "welcome.password.exchange";

    @Bean
    public TopicExchange passwordRecoveryExchange() {
        return new TopicExchange(PASSWORD_RECOVERY_EXCHANGE);
    }

    @Bean
    public TopicExchange healthEndpointExchange() {
        return new TopicExchange(HEALTH_ENDPOINT_EXCHANGE);
    }

    @Bean
    public TopicExchange welcomePasswordExchange() {
        return new TopicExchange(WELCOME_PASSWORD_EXCHANGE);
    }


    @Bean
    public Binding bindingPasswordRecovery() {
        return BindingBuilder.bind(passwordRecoveryQueue())
                .to(passwordRecoveryExchange())
                .with(ROUTING_KEY_PASSWORD_RECOVERY);
    }

    @Bean
    public Binding bindingHealthEndpoint() {
        return BindingBuilder.bind(healthEndpointDownQueue())
                .to(healthEndpointExchange())
                .with(ROUTING_KEY_HEALTH_ENDPOINT_DOWN);
    }

    @Bean
    public Binding bindingWelcomePassword() {
        return BindingBuilder.bind(welcomePasswordQueue())
                .to(welcomePasswordExchange())
                .with(ROUTING_KEY_WELCOME_PASSWORD);
    }

    @Bean
    public Queue welcomePasswordQueue() {
        return new Queue(WELCOME_PASSWORD_QUEUE, true);
    }



    @Bean
    public Queue passwordRecoveryQueue() {
        return new Queue(PASSWORD_RECOVERY_QUEUE, true);
    }

    @Bean
    public Queue healthEndpointDownQueue() {
        return new Queue(HEALTH_ENDPOINT_DOWN_QUEUE, true);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


}
