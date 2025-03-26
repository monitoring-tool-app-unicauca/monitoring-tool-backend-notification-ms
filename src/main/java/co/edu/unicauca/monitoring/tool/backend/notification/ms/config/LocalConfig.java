package co.edu.unicauca.monitoring.tool.backend.notification.ms.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class LocalConfig {

    // ~ Beans
    // ====================================================================

    /**
     * Configures a MessageSource bean to resolve messages from properties files.
     *
     * @return Configured MessageSource bean
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
