package com.zccc.manager;

import com.zccc.util.AiManager;
import com.zccc.util.GetMessage;
import org.springframework.stereotype.Component;

@Component("tencent")
public class TencentService implements AiManager {
    @Override
    public String doChat(long modelId, String message) throws Exception {
        String s= GetMessage.send(message);
        System.out.println("腾讯返回："+s);
        return s;
    }
}
