package com.mahama.parent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("mahama.config.ret.code")
@Data
public class RetCodeConfig {
    private Integer success = 20000;
    private Integer failure = 99999;
    private Integer expire = 50014;
}
