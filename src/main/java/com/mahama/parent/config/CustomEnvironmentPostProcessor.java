package com.mahama.parent.config;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;

import java.util.Properties;

public class CustomEnvironmentPostProcessor implements EnvironmentPostProcessor {
    @SneakyThrows
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Resource resource = new FileUrlResource("config/application-custom.properties");
        if(resource.exists()){
            Properties properties = new Properties();
            properties.load(resource.getInputStream());
            PropertySource<?> propertySource=new PropertiesPropertySource(resource.getFilename(), properties);
            environment.getPropertySources().addFirst(propertySource);
        }
    }
}
