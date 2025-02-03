package com.zccc.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.tencentcloudapi.common.*;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.hunyuan.v20230901.HunyuanClient;
import com.tencentcloudapi.hunyuan.v20230901.models.ChatCompletionsRequest;
import com.tencentcloudapi.hunyuan.v20230901.models.ChatCompletionsResponse;
import com.tencentcloudapi.hunyuan.v20230901.models.Message;

import java.lang.reflect.Type;


public class GetMessage
{
    public static class CustomSSEResponse extends SSEResponseModel {
        public String Response;

        private String RequestId;

        @Override
        public String getRequestId() {
            return RequestId;
        }

        @Override
        public void setRequestId(String requestId) {
            RequestId = requestId;
        }
    }

    static class CustomSSEResponseDeserializer implements JsonDeserializer<CustomSSEResponse> {
        @Override
        public CustomSSEResponse deserialize(JsonElement jsonElement, Type type,
                                             JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            CustomSSEResponse resp = new CustomSSEResponse();
            resp.Response = jsonElement.toString();
            return resp;
        }
    }
    public static String send(String user) {
        System.out.println("请求：：：："+user);
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential("", "");
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("hunyuan.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            HunyuanClient client = new HunyuanClient(cred, "", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            ChatCompletionsRequest req = new ChatCompletionsRequest();
            req.setModel("hunyuan-lite");

            Message[] messages = new Message[5];
            Message message1 = new Message();
            message1.setRole("user");
            message1.setContent("你是一位数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容： \n分析需求：\n{数据分析的需求或目标}\n 原始数据：\n{CSV格式的原始数据，用,作分隔符}\n请根据这两部分的内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n【【【【【\n{前端Echarts V5 的 option 配置对象JS代码。合理地将数据进行可视化不要生成任何多余的内容例如注释}\n【【【【【\n{数据分析结论，越详细越好，不要生成过多解释}\n\n分析需求：\n分析一下，请使用柱状图\n原始数据：\n日期,用户数\n1号,10\n2号,20\n3号,30\n4号,90\n5号,0\n6号,10\n7号,20");
            messages[0] = message1;

            Message message2 = new Message();
            message2.setRole("assistant");
            message2.setContent("【【【【【\n{\n" +
                    "    \"title\": {\n" +
                    "        \"text\": \"用户数柱状图\"\n" +
                    "    },\n" +
                    "    \"tooltip\": {\n" +
                    "        \"trigger\": \"axis\"\n" +
                    "    },\n" +
                    "    \"legend\": {\n" +
                    "        \"data\": [\"用户数\"]\n" +
                    "    },\n" +
                    "    \"grid\": {\n" +
                    "        \"left\": \"3%\",\n" +
                    "        \"right\": \"4%\",\n" +
                    "        \"bottom\": \"3%\",\n" +
                    "        \"containLabel\":true\n" +
                    "    },\n" +
                    "    \"xAxis\": {\n" +
                    "        \"type\": \"category\",\n" +
                    "        \"boundaryGap\":false,\n" +
                    "        \"data\": [\"1号\", \"2号\", \"3号\", \"4号\", \"5号\", \"6号\", \"7号\"]\n" +
                    "    },\n" +
                    "    \"yAxis\": {\n" +
                    "        \"type\": \"value\"\n" +
                    "    },\n" +
                    "    \"series\": [\n" +
                    "        {\n" +
                    "            \"name\": \"用户数\",\n" +
                    "            \"type\": \"bar\",\n" +
                    "            \"data\": [10, 20, 30, 90, 0, 10, 20]\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}\n【【【【【\n数据分析结论：\n根据折线图可以看出，用户数在1号至3号期间呈现稳步增长的趋势，3号达到峰值90。然而，在4号用户数突然下降至0，这可能是由于某个事件或日历日期导致用户流失。随后，在5号和6号用户数再次回升至10和20，但在7号又降至20。整体来看，用户数的变化呈现出波动性，需要进一步分析用户流失的原因以及用户增长的动力。\n");
            messages[1] = message2;

            Message message3 = new Message();
            message3.setRole("user");
            message3.setContent("你是一位数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容： \n分析需求：\n{数据分析的需求或目标}\n 原始数据：\n{CSV格式的原始数据，用,作分隔符}\n请根据这两部分的内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n【【【【【\n{前端Echarts V5 的 option 配置对象JS代码。合理地将数据进行可视化不要生成任何多余的内容例如注释}\n【【【【【\n{数据分析结论，越详细越好，不要生成过多解释}\n\n分析需求：\n分析一下，请使用饼状图\n原始数据：\n日期,用户数\n1号,10\n2号,20\n3号,30\n4号,90\n5号,0\n6号,10\n7号,20");
            messages[2] = message3;
            Message message4 = new Message();
            message4.setRole("assistant");
            message4.setContent("【【【【【\n{\n" +
                    "    \"title\": {\n" +
                    "        \"text\": \"用户数饼图\"\n" +
                    "    },\n" +
                    "    \"tooltip\": {\n" +
                    "        \"trigger\": \"item\"\n" +
                    "    },\n" +
                    "    \"legend\": {\n" +
                    "        \"data\": [\"用户数\"]\n" +
                    "    },\n" +
                    "    \"series\": [\n" +
                    "        {\n" +
                    "            \"name\": \"用户数\",\n" +
                    "            \"type\": \"pie\",\n" +
                    "            \"radius\": \"50%\",\n" +
                    "            \"data\": [\n" +
                    "                {\"value\": 10, \"name\": \"1号\"},\n" +
                    "                {\"value\": 20, \"name\": \"2号\"},\n" +
                    "                {\"value\": 30, \"name\": \"3号\"},\n" +
                    "                {\"value\": 90, \"name\": \"4号\"},\n" +
                    "                {\"value\": 0, \"name\": \"5号\"},\n" +
                    "                {\"value\": 10, \"name\": \"6号\"},\n" +
                    "                {\"value\": 20, \"name\": \"7号\"}\n" +
                    "            ]\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}\n【【【【【\n数据分析结论：\n根据饼状图可以看出，用户数在1号至3号期间呈现稳步增长的趋势，3号达到峰值90。然而，在4号用户数突然下降至0，这可能是由于某个事件或日历日期导致用户流失。随后，在5号和6号用户数再次回升至10和20，但在7号又降至20。整体来看，用户数的变化呈现出波动性，需要进一步分析用户流失的原因以及用户增长的动力。\n");
            messages[3] = message4;

            Message message5 = new Message();
            message5.setRole("user");
            message5.setContent("你是一位数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容，请你分析出图表和分析结论： \n" +
                    "分析需求：\n" +
                    "{数据分析的需求或目标}\n" +
                    " 原始数据：\n" +
                    "{CSV格式的原始数据，用,作分隔符}\n" +
                    "请根据这两部分的内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
                    "【【【【【\n" +
                    "{前端Echarts V5 的 option 配置对象JS代码。合理地将数据进行可视化不要生成任何多余的内容例如注释}\n" +
                    "【【【【【\n" +
                    "{数据分析结论，越详细越好，不要生成过多解释}\n\n"+user);
            messages[4] = message5;

            req.setMessages(messages);

            // 返回的resp是一个ChatCompletionsResponse的实例，与请求对象对应
            ChatCompletionsResponse resp = client.ChatCompletions(req);
            // 输出json格式的字符串回包
            if (resp.isStream()) { // 流式响应
                for (SSEResponseModel.SSE e : resp) {
                    System.out.println(e.Data);
                }
            } else { // 非流式响应
                return prace(AbstractModel.toJsonString(resp));
            }
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
        return null;
    }
    public static String prace(String jsonString) {
        // 假设 jsonString 是输入的 JSON 字符串

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // 获取 "Content" 字段
            JsonNode contentNode = rootNode.path("Choices").get(0).path("Message").path("Content");

            // 输出 "Content" 字段的值
            return contentNode.asText();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
