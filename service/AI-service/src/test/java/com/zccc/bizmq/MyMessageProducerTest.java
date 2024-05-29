package com.zccc.bizmq;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class MyMessageProducerTest {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     * @param
     */
    @Test
    public void sendMessage() {
        rabbitTemplate.convertAndSend(AIMqConstant.BI_EXCHANGE_NAME, AIMqConstant.BI_ROUTING_KEY, "nono");
    }

}
