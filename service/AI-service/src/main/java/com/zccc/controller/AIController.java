package com.zccc.controller;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.SSEResponseModel;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.hunyuan.v20230901.HunyuanClient;
import com.tencentcloudapi.hunyuan.v20230901.models.ChatCompletionsRequest;
import com.tencentcloudapi.hunyuan.v20230901.models.ChatCompletionsResponse;
import com.tencentcloudapi.hunyuan.v20230901.models.Choice;
import com.tencentcloudapi.hunyuan.v20230901.models.Message;
import com.zccc.bizmq.AIMessageProducer;
import com.zccc.common.ChatStdResponse;
import com.zccc.model.dto.chart.GenChartByAiRequest;
import com.zccc.service.ChartService;
import com.zccc.exception.BusinessException;
import com.zccc.exception.ThrowUtils;
import com.zccc.manager.RedisLimiterManager;

import com.zccc.service.UserService;
import com.zccc.utils.ExcelUtils;
import com.zccc.common.BaseResponse;
import com.zccc.common.ErrorCode;
import com.zccc.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import com.zccc.model.entity.Chart;
import com.zccc.model.entity.User;
import com.zccc.model.vo.BiResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

import static com.zccc.common.Constants.ONE_MB;
import static com.zccc.common.Constants.validFileSuffixList;
import static com.zccc.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 帖子接口
 *

 */
@RestController
@RequestMapping("/ai")
@Slf4j
public class AIController {


    @DubboReference
    private UserService userService;

    @Resource
    private RedisLimiterManager redisLimiterManager;


    @Resource
    private AIMessageProducer biMessageProducer;

    @DubboReference(check = false)
    private ChartService chartService;

    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        //微服务调用
        long userId = currentUser.getId();
        currentUser = userService.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }


    /**
     * 智能分析（异步消息队列）
     *
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen/async/mq")
    public BaseResponse<BiResponse> genChartByAiAsyncMq(@RequestPart("file") MultipartFile multipartFile,
                                                      GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();
        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");
        // 校验文件
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        // 校验文件大小
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件超过 1M");
        // 校验文件后缀 aaa.png
        String suffix = FileUtil.getSuffix(originalFilename);
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");

        User loginUser = getLoginUser(request);
        // 限流判断，每个用户一个限流器
        //todo 后期抽象成注解
        redisLimiterManager.doRateLimit("genChartByAi_" + loginUser.getId());


        String csvData = ExcelUtils.excelToCsv(multipartFile);

        //先保存，然后把id放到mq中，再从数据库中查询，
        // 插入到数据库
        Chart chart =Chart.builder().name(name).goal(goal).chartData(csvData)
                .chartType(chartType).userId(loginUser.getId()).status("wait").build();
        log.info("保存图表------>{}", chart.getName());
        Long saveResult = chartService.save_chart(chart);
        if(saveResult==-1){
            handleChartUpdateError(chart.getId());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图表保存失败");
        }

        long newChartId = saveResult;
        log.info("成功保存：{}，↓向消息队列发送新id", chart.getName());
        biMessageProducer.sendMessage(String.valueOf(newChartId));
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(newChartId);
        return ResultUtils.success(biResponse);
    }


    private void handleChartUpdateError(long chartId) {
        Chart updateChartResult = Chart.builder().id(chartId).status("failed").execMessage("保存图表失败").build();
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult) {
            log.error("更新图表失败状态失败" + chartId + "," + "保存图表失败");
        }
    }

//    @GetMapping("/stream")
//    public String streamRes(String wd, HttpServletResponse response) throws IOException {
//        ServletOutputStream outputStream = response.getOutputStream();
//        try {
//            //核心设置数据流格式响应头
//            response.setContentType("text/event-stream");
//            response.setCharacterEncoding("UTF-8");
//            HttpProfile httpProfile = new HttpProfile();
//            httpProfile.setEndpoint("hunyuan.tencentcloudapi.com");
//            // 实例化一个client选项，可选的，没有特殊需求可以跳过
//            ClientProfile clientProfile = new ClientProfile();
//            clientProfile.setHttpProfile(httpProfile);
//            Credential cred = new Credential("AKID9CMK5va3LZhfTBqLfoldNwLagbLvLcjm", "y9bGrRuNp9JLDvT8nJg1JTUUiqqofP0Y");
//            HunyuanClient client = new HunyuanClient(cred, "", clientProfile);
//            ChatCompletionsRequest req = new ChatCompletionsRequest();
//            Message msg = new Message();
//            req.setModel("hunyuan-lite");
//            msg.setRole("user");
//            msg.setContent(wd);
//            req.setMessages(new Message[]{msg});
//            req.setStream(true);
//            ChatCompletionsResponse resp = client.ChatCompletions(req);
//            System.out.println(resp);
//            if (req.getStream()) {
//                for (SSEResponseModel.SSE e : resp) {
//                    ChatStdResponse eventModel = JSONObject.toJavaObject(JSONObject.parseObject(e.Data), ChatStdResponse.class);
//                    List<Choice> choices = eventModel.getChoices();
//                    if (choices.size() > 0) {
//                        String res = choices.get(0).getDelta().getContent();
//                        outputStream.write(res.getBytes());
//                        outputStream.flush();
//                    }
//                    // 如果希望在任意时刻中止事件流, 使用 resp.close() + break
//                    boolean iWantToCancelNow = false;
//                    if (iWantToCancelNow) {
//                        outputStream.close();
//                        resp.close();
//                        break;
//                    }
//                }
//            }
//        } catch (TencentCloudSDKException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
    @GetMapping("/stream")
    public void streamRes(String wd, HttpServletResponse response) throws IOException {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();

        try {
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("hunyuan.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            Credential cred = new Credential("AKID9CMK5va3LZhfTBqLfoldNwLagbLvLcjm", "y9bGrRuNp9JLDvT8nJg1JTUUiqqofP0Y");
            HunyuanClient client = new HunyuanClient(cred, "", clientProfile);

            ChatCompletionsRequest req = new ChatCompletionsRequest();
            Message msg = new Message();
            req.setModel("hunyuan-lite");
            msg.setRole("user");
            msg.setContent(wd);
            req.setMessages(new Message[]{msg});
            req.setStream(true);

            ChatCompletionsResponse resp = client.ChatCompletions(req);

            if (req.getStream()) {
                for (SSEResponseModel.SSE e : resp) {
                    ChatStdResponse eventModel = JSONObject.toJavaObject(JSONObject.parseObject(e.Data), ChatStdResponse.class);
                    List<Choice> choices = eventModel.getChoices();

                    if (!choices.isEmpty()) {
                        String res = choices.get(0).getDelta().getContent();
                        String formattedResponse = "data: " + res + "\n\n";
//                        String formattedResponse = "data: " + res + "\n\n";
                        outputStream.write(formattedResponse.getBytes("UTF-8"));
                        outputStream.flush();
                    }

                    // 如果需要取消
                    boolean iWantToCancelNow = false;
                    if (iWantToCancelNow) {
                        outputStream.close();
                        resp.close();
                        break;
                    }
                }
            }
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        } finally {
            outputStream.close(); // 确保输出流关闭
        }
    }



}
