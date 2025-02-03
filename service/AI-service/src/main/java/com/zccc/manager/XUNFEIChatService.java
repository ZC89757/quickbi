package com.zccc.manager;

import com.zccc.common.ErrorCode;
import com.zccc.exception.BusinessException;
import com.zccc.util.AiManager;
import com.zccc.util.BigModelNew;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("xunfei")
public class XUNFEIChatService implements AiManager {

    @Override
    public String doChat(long modelId, String message) throws Exception {
        String res=new BigModelNew().send(message);
        if(res.equals(""))throw new BusinessException(ErrorCode.SYSTEM_ERROR,"ai错误");
        return res;
    }
}
