package com.mahama.parent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author mahama
 * @date 2023年05月04日
 */
@Configuration
@ConfigurationProperties("application")
@Data
public class ApplicationConfig {
    private String version;
    private String buildTime;
}
