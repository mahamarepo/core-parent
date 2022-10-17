package com.mahama.parent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("dynamic")
@Data
public class DynamicTableConfig {
    private String tableName = "";
}
