package com.coding.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author guanweiming
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({
        AppProperties.class,
})
public class AppConfiguration {
    private final AppProperties appProperties;

    public AppConfiguration(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Bean
    public AppProperties appProperties() {
        return appProperties;
    }


    @ConditionalOnExpression("!'${spring.profiles.active}'.equals('local')")
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
