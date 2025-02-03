package com.zccc.manager;

import com.zccc.exception.BusinessException;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import com.zccc.util.AiManager;
import com.zccc.util.BigModelNew;
import com.zccc.util.YCMClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用于对接 AI 平台
 */
@Slf4j
@Service("yucongming")
public class YCMChatService implements AiManager {

    @Resource
    private YCMClient yuCongMingClient;


    /**
     * AI 对话
     *
     * @param modelId
     * @param message
     * @return
     */
    @Override
    public String doChat(long modelId, String message)throws BusinessException {
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(modelId);
        devChatRequest.setMessage(message);
        System.out.println("发送请求=========》"+devChatRequest);
        BaseResponse<DevChatResponse> response;
        System.out.println("调用api");
        response = yuCongMingClient.doChat(devChatRequest);
        System.out.println(response);
        return response.getData().getContent().replace("'", "\"");
    }
}
