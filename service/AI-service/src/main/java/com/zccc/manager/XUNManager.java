package com.zccc.manager;

import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.SparkSyncChatResponse;
import io.github.briqt.spark4j.model.request.SparkRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class XUNManager {

    @Resource
    private SparkClient sparkClient;

    /**
     * AI 生成问题的预设条件
     */
//    public static final String PRECONDITION = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
//            "分析需求：\n" +
//            "（数据分析的需求或目标)\n" +
//            "原始数据：\n" +
//            "(csv格式的原始数据，用,作为分隔符)\n" +
//            "请提取出内容的结论，用指定格式生成此内容（此外不要输出任何多余的开头、结尾、注释）\n" +
//            "【【【【【\n" +
//            "前端 Echarts V5 的 option 配置对象 JSON 代码，不要生成任何多余的内容，比如注释和代码块标记\n" +
//            "】】】】】\n" +
//            "前端的数据分析结论，越详细越好，不要生成多余的注释）\n" +
//            "最终结论：前端代码 【【【【【 分析结论 】】】】】\n";
    public static final String PRECONDITION = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
                "分析需求：\n" +
                "{数据分析的需求或者目标}\n" +
                "原始数据：\n" +
                "{csv格式的原始数据，用,作为分隔符}\n" +
                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
                "【【【【【\n" +
                "{前端 Echarts V5 的 option 配置对象js代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释}\n" +
                "【【【【【\n" +
                "{明确的数据分析结论、越详细越好，不要生成多余的注释}\n";

    /**
     * 向讯飞 AI 发送请求
     *
     * @return
     */
    public String sendMesToAIUseXingHuo(final String content) {
        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.userContent(content));

        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                .messages(messages)
                // 模型回答内容tokens的最大长度, 非必传, 取值为[1,4096], 默认值为2048
                .maxTokens(2048)
                // 核采样随机值. 用于决定结果随机性, 取值越高随机性越强即相同的问题得到的不同答案的可能性越高 非必传, 取值为[0.1,1], 默认值为0.5
                .temperature(0.2)
                // 指定请求版本 这个版本根据自己的 API 版本进行修改
                .apiVersion(SparkApiVersion.V3_5)
                .build();

        // 同步调用
        System.out.println(content);
        SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
        System.out.println(sparkRequest);
        String responseContent = chatResponse.getContent();
        log.info("星火 AI 返回的结果 {}", responseContent);
        return responseContent;
    }
}
