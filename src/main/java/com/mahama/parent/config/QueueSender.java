package com.mahama.parent.config;

import com.mahama.common.enumeration.QueueFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("QueueSender_core")
@Slf4j
public class QueueSender {
    /**
     * 连接工厂
     */
    @Bean(QueueFactory.PREFETCH_COUNT_FACTORY_50)
    public SimpleRabbitListenerContainerFactory PREFETCH_COUNT_FACTORY_50(SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setPrefetchCount(50);
        configurer.configure(factory, connectionFactory);
        return factory;
    }
}
