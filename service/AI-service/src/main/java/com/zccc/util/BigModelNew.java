package com.zccc.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@NoArgsConstructor
@Slf4j
@Component
public class BigModelNew extends WebSocketListener {
    // 地址与鉴权信息  https://spark-api.xf-yun.com/v1.1/chat   1.5地址  domain参数为general
    // 地址与鉴权信息  https://spark-api.xf-yun.com/v2.1/chat   2.0地址  domain参数为generalv2
    public static final String hostUrl = "https://spark-api.xf-yun.com/v3.5/chat";
    public static final String appid = "fad73e27";
    public static final String apiSecret = "";
    public static final String apiKey = "";

    public static List<RoleContent> historyList=new ArrayList<>(); // 对话历史存储集合

    public String totalAnswer=""; // 大模型的答案汇总

    public volatile boolean isOK=false;

    // 环境治理的重要性  环保  人口老龄化  我爱我的祖国
    public String NewQuestion = "";

    public static final Gson gson = new Gson();

    // 个性化参数
    private String userId;
    private Boolean wsCloseFlag;

    private static Boolean totalFlag=true; // 控制提示用户是否输入
    // 构造函数
    public BigModelNew(String userId, Boolean wsCloseFlag,String NewQuestion) {
        this.userId = userId;
        this.NewQuestion = NewQuestion;
        this.wsCloseFlag = wsCloseFlag;
    }

    // 主函数
    public  String send(String cvs) throws Exception {
        this.NewQuestion=cvs;
        String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
        OkHttpClient client = new OkHttpClient.Builder().build();
        String url = authUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
        BigModelNew bigModelNew = new BigModelNew(0 + "", false,NewQuestion);
        Request request = new Request.Builder().url(url).build();
        client.newWebSocket(request, bigModelNew);
        while(!bigModelNew.isOK){Thread.sleep(1000);};
        System.out.println("原始result=  "+bigModelNew.totalAnswer);
        return bigModelNew.totalAnswer;
    }



    public static boolean canAddHistory(){  // 由于历史记录最大上线1.2W左右，需要判断是能能加入历史
        int history_length=0;
        for(RoleContent temp:historyList){
            history_length=history_length+temp.content.length();
        }
        if(history_length>12000){
            historyList.remove(0);
            historyList.remove(1);
            historyList.remove(2);
            historyList.remove(3);
            historyList.remove(4);
            return false;
        }else{
            return true;
        }
    }

    // 线程来发送音频与参数
    class MyThread extends Thread {
        private WebSocket webSocket;

        public MyThread(WebSocket webSocket) {
            this.webSocket = webSocket;
        }

        public void run() {
            try {
                JSONObject requestJson=new JSONObject();

                JSONObject header=new JSONObject();  // header参数
                header.put("app_id",appid);
                header.put("uid",UUID.randomUUID().toString().substring(0, 10));

                JSONObject parameter=new JSONObject(); // parameter参数
                JSONObject chat=new JSONObject();
                chat.put("domain","generalv3.5");
                chat.put("temperature",0.1);
                chat.put("max_tokens",4096);
                parameter.put("chat",chat);

                JSONObject payload=new JSONObject(); // payload参数
                JSONObject message=new JSONObject();
                JSONArray text=new JSONArray();

                // 历史问题获取
                if(historyList.size()>0){
                    for(RoleContent tempRoleContent:historyList){
                        text.add(JSON.toJSON(tempRoleContent));
                    }
                }


                RoleContent roleContent1=new RoleContent();
                roleContent1.role="system";
                roleContent1.content="你是一位数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容，请你分析出图表和分析结论," +
                        "\n分析需求：" +
                        "\n{数据分析的需求或目标}" +
                        "\n 原始数据：" +
                        "\n{CSV格式的原始数据，用,作分隔符}" +
                        "\n请根据这两部分的内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）" +
                        "\n【【【【【" +
                        "\n{前端Echarts V5 的 option 配置对象JSON代码。合理地将数据进行可视化不要生成任何多余的内容例如注释}" +
                        "\n【【【【【" +
                        "\n{数据分析结论，越详细越好，不要生成过多解释}";
                text.add(JSON.toJSON(roleContent1));
                historyList.add(roleContent1);
                // 最新问题
                RoleContent roleContent=new RoleContent();
                roleContent.role="user";
                roleContent.content=NewQuestion;
                text.add(JSON.toJSON(roleContent));
                historyList.add(roleContent);


                message.put("text",text);
                payload.put("message",message);


                requestJson.put("header",header);
                requestJson.put("parameter",parameter);
                requestJson.put("payload",payload);
                // System.err.println(requestJson); // 可以打印看每次的传参明细
                webSocket.send(requestJson.toString());
                // 等待服务端返回完毕后关闭
                while (true) {
                    // System.err.println(wsCloseFlag + "---");
                    Thread.sleep(200);
                    if (wsCloseFlag) {
                        break;
                    }
                }
                webSocket.close(1000, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            isOK=true;
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        MyThread myThread = new MyThread(webSocket);
        myThread.start();
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        // System.out.println(userId + "用来区分那个用户的结果" + text);
        JsonParse myJsonParse = gson.fromJson(text, JsonParse.class);
        if (myJsonParse.header.code != 0) {
            System.out.println("发生错误，错误码为：" + myJsonParse.header.code);
            System.out.println("本次请求的sid为：" + myJsonParse.header.sid);
            webSocket.close(1000, "");
        }
        List<Text> textList = myJsonParse.payload.choices.text;
        for (Text temp : textList) {
//            System.out.print(temp.content);
            totalAnswer=totalAnswer+temp.content;
        }
//        System.out.print(totalAnswer);
        if (myJsonParse.header.status == 2) {
            if(canAddHistory()){
                RoleContent roleContent=new RoleContent();
                roleContent.setRole("assistant");
                roleContent.setContent(totalAnswer);
                historyList.add(roleContent);
            }else{
                historyList.remove(0);
                RoleContent roleContent=new RoleContent();
                roleContent.setRole("assistant");
                roleContent.setContent(totalAnswer);
                historyList.add(roleContent);
            }
            wsCloseFlag = true;
            totalFlag=true;
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        try {
            if (null != response) {
                int code = response.code();
                System.out.println("onFailure code:" + code);
                System.out.println("onFailure body:" + response.body().string());
                if (101 != code) {
                    System.out.println("connection failed");
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    // 鉴权方法
    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // 拼接
        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";
        // System.err.println(preStr);
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        // System.err.println(sha);
        // 拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().//
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();

        // System.err.println(httpUrl.toString());
        return httpUrl.toString();
    }

    //返回的json结果拆解
    class JsonParse {
        Header header;
        Payload payload;
    }

    class Header {
        int code;
        int status;
        String sid;
    }

    class Payload {
        Choices choices;
    }

    class Choices {
        List<Text> text;
    }

    class Text {
        String role;
        String content;
    }
    class RoleContent{
        String role;
        String content;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
