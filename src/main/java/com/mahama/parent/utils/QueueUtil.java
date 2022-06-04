package com.mahama.parent.utils;

import com.mahama.parent.enumeration.AsyncList;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Async;

public class QueueUtil {
    private static AmqpTemplate template = SpringBeanUtil.getBean(AmqpTemplate.class);
    private static AmqpAdmin amqpAdmin = SpringBeanUtil.getBean(AmqpAdmin.class);

    private static MessagePostProcessor getArrayMessagePostProcessor() {
        return message -> {
            message.getMessageProperties().setType("array");
            return message;
        };
    }

    private static MessagePostProcessor getMessagePostProcessor() {
        return message -> message;
    }

    public static QueueUtil me() {
        return new QueueUtil();
    }

    /**
     * 发送对象数据到队列
     *
     * @param queueName 队列名称
     * @param data      数据
     * @param <T>       数据类
     */
    @Async(AsyncList.QUEUE_POOL)
    public <T> void sendObject(String queueName, T data) {
        template.convertAndSend(queueName, data, getMessagePostProcessor());
    }

    /**
     * 发送数组数据到队列
     *
     * @param queueName 队列名称
     * @param data      数据
     * @param <T>       数据类
     */
    @Async(AsyncList.QUEUE_POOL)
    public <T> void sendArray(String queueName, T data) {
        template.convertAndSend(queueName, data, getArrayMessagePostProcessor());
    }

    /**
     * 发送对象数据到队列
     *
     * @param exchangeName 交换器名称
     * @param routingKey   路由
     * @param data         数据
     * @param <T>          数据类
     */
    @Async(AsyncList.QUEUE_POOL)
    public <T> void sendObject(String exchangeName, String routingKey, T data) {
        template.convertAndSend(exchangeName, routingKey, data, getMessagePostProcessor());
    }

    @Async(AsyncList.QUEUE_POOL)
    public <T> T receiveAndConvert(String queueName) {
        return template.receiveAndConvert(queueName, new ParameterizedTypeReference<>() {
        });
    }

    /**
     * 清理队列
     *
     * @param queueName 队列名称
     * @return 清理数量
     */
    @Async(AsyncList.QUEUE_POOL)
    public int purgeQueue(String queueName) {
        return amqpAdmin.purgeQueue(queueName);
    }
}
