package com.sms.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.sms.api.service.SMSService;
import com.sms.api.model.SMSBody;
import com.sms.api.service.RedisService;
import com.sms.api.util.PatternUtil;
import com.sms.api.util.UserInfoUtil;
import io.swagger.annotations.Api;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: api
 * @description: 消息处理
 * @author: 邓太阳
 * @create: 2020-06-19 09:06
 **/
//@Api(value = "MessageController", description = "验证码 API", tags = "消息支持-API")
@RestController
@RequestMapping("sms/api")
public class MessageController {

    @Resource
    RedisService redisService;

    @Autowired
    SMSService smsService;

    @Bean(name = "stringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory
                                                           redisConnectionFactory){
        return new StringRedisTemplate(redisConnectionFactory);
    }


    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    UserInfoUtil userInfoUtil;

    private Pattern comPattern = Pattern.compile("COM\\d+");


    public String getKV(String content) {
//        if (!content.contains("快手")) {
//            return null;
//        }
        Matcher comMatcher = comPattern.matcher(content);
        if (comMatcher.find()) {
            String com = comMatcher.group(0);
            return com+"#"+content;
        }else {
            return "";
        }
    }

    public boolean checkGrounp(String id){
        String[] groups = {
                "914646378",
                "218832300",//AR
                "943557709",//雨双
                "654047694"//追梦
        };
        for (String gpid:groups){
            if (id.equalsIgnoreCase(gpid)) {
                System.out.println(gpid);
                return true;
            }
        }


        return false;
    }


    /*** 
     * @Description: 接收消息
     * @Param: [body] 
     * @return: java.util.Map<java.lang.String,java.lang.Object> 
     * @Author: 邓太阳 
     * @Date: 2020-06-19 09:11
     */ 
    @RequestMapping("/info")
    public Map<String,Object> info(@RequestBody SMSBody body){
        if (checkGrounp(body.getGroup_id())) {
            String content = body.getMessage();
            String code = PatternUtil.getyzm(content,6);
            if (StringUtils.isEmpty(code)) {
                code = PatternUtil.getyzm(content,4);
            }
            if (StringUtils.isEmpty(code)) {
                System.out.println("抓码异常--"+content);
                return null;
            }
            String key = PatternUtil.between(content,"COM","\\s");
            if (StringUtils.isEmpty(key)) {
                System.out.println("抓码异常--"+content);
            }else {
                redisService.set("COM"+key,code,24*60*60L);
                System.out.println("COM"+key+"----"+code);
            }
        }
        return null;
    }



    @GetMapping("/getMessage")
    public Map<String,Object> getMessage(String phone,String sign){
        Map<String,Object> data = new HashMap<>(3);
        if (redisService.hasKey(phone)) {
            data.put("code","1");
            data.put("message",stringRedisTemplate.opsForValue().get(phone));
            System.out.println("消费---"+phone);
            return data;
        }else {
            data.put("code","0");
        }
        return data;
    }

    @GetMapping("/getCount")
    public String getMessage() throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // 创建HttpGet请求，相当于在浏览器输入地址
        HttpGet httpGet = new HttpGet("https://www.csfaka.com/ajax/getgoodinfo?goodid=28919");

        CloseableHttpResponse response = null;
        try {
            // 执行请求，相当于敲完地址后按下回车。获取响应
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                // 解析响应，获取数据
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                Map<String,Object> data = (Map<String, Object>) JSONObject.parse(content);
                System.out.println(data.get("goodinvent"));
                return "<div align=\"center\">"+data.get("goodinvent").toString()+"</div>";
            }
        } finally {
            if (response != null) {
                // 关闭资源
                response.close();
            }
            // 关闭浏览器
            httpclient.close();
        }
        return "";
    }



    @GetMapping("/getUser")
    public Map<String,Object> getOrder() throws IOException {
        Map<String,Object> data = new HashMap<>();
        data.put("head", userInfoUtil.getUserHead());
        data.put("name", userInfoUtil.getUserName());
        return data;
    }

    public static void main(String[] args) {
        String d = "[23:46:05] COM276 156*****690 【快手科技】743603快手验证码，15分钟内有效，仅用于关闭账号保护功能，请勿告知他人。\n" +
                "「售后请联系订单上卖家QQ」";
        System.out.println(PatternUtil.getyzm(d,4));
        System.out.println(PatternUtil.getyzm(d,6));
        System.out.println(PatternUtil.between(d,"COM","\\s"));



    }





}
