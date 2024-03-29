package com.mahama.parent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("mahama.config.ret.temp")
@Data
public class RetTempConfig {
    private Boolean tempIgnore = false;
}
