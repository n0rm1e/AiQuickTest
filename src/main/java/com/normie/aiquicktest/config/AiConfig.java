package com.normie.aiquicktest.config;

import com.zhipu.oapi.ClientV4;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ai")
@Data
public class AiConfig {
    private String apiKey;

    @Bean
    public ClientV4 getClientV4() {
        ClientV4 client = new ClientV4.Builder(apiKey).build();
        return client;
    }
}
