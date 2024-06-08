package com.zccc.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class AiManagerTest {

    @Resource
    private XUNManager xunManager;

    @Test
    void doChat() {
        String answer = xunManager.sendMesToAIUseXingHuo(XUNManager.PRECONDITION);
//        System.out.println(answer);
    }
}
