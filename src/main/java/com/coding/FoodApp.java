package com.coding;

import com.coding.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author guanweiming
 */
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
@EnableAsync
@SpringBootApplication(exclude = RabbitAutoConfiguration.class)
public class FoodApp implements CommandLineRunner {
    private final AppProperties appProperties;

    public static void main(String[] args) {
        SpringApplication.run(FoodApp.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("应用启动完成");
    }
}
