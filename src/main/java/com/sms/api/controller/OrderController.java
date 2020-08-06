package com.sms.api.controller;

import com.alibaba.fastjson.JSON;
import com.sms.api.dao.SMSDao;
import com.sms.api.model.SmsEnt;
import com.sms.api.service.RedisService;
import com.sms.api.util.HttpClientUtil;
import com.sms.api.util.PatternUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: api
 * @description: 订单功能
 * @create: 2020-06-20 22:35
 **/
//@Api(value = "OrderController", description = "昌顺-Order API", tags = "昌顺-API")
@RequestMapping("order/api")
@RestController
public class OrderController {

    @Value("${cs.cookie}")
    private String cookie;

    @Autowired
    SMSDao smsDao;
    @Resource
    RedisService redisService;

    @GetMapping("/buy1")
    public String buy(String num) throws IOException {
        if ("".equalsIgnoreCase(num) || null == num) {
            num = "1";
        }
        String respone = HttpClientUtil.get("https://www.csfaka.com/pay/order?contact=104561&userid=29181&agent_goodsid=51639&kucun=135&feePayer=1&pid=230&goodid=28919&quantity="+num);
        String orderId = PatternUtil.between(respone,"<input type=\"hidden\" name=\"trade_no\" value=\"","\">");
        String data = HttpClientUtil.get("https://www.csfaka.com/index/pay/payment?trade_no="+orderId);
        String img = PatternUtil.between(data,"<!--<img src=\"","\" width=\"210px\" height=\"210px\">-->");
        return "<div align=\"center\"><img src=\""+img+"\"><h3><a href=\"getOrder?orderid="+orderId+"\">"+orderId+"</a></h3></div>";
    }

    @GetMapping("/getOrder")
    public String getOrder(String orderid) throws IOException {
        if (StringUtils.isEmpty(orderid)) {
            return "<h1>别乱试，兄嘚</h1>";
        }
        if (redisService.get(orderid+"data")!=null) {
            return (String) redisService.get(orderid+"data");
        }
        Map<String,String> head = new HashMap<>();
        head.put("cookie","waf_cookie=85d27008-bc13-4531933869a1b1acc4db9eddca92c799e7e6; s17f4db19=k5esjr7jnfqbntu6un54nusbu3; __51cke__=; freeze_money_tip=1; has_merchant_backend_one=1; __tins__20416941=%7B%22sid%22%3A%201592665165273%2C%20%22vd%22%3A%2023%2C%20%22expires%22%3A%201592667948513%7D; __51laig__=26");

        String respone = HttpClientUtil.get("https://www.csfaka.com/merchant/agent/myorder",head);
        List<String> orderIdcs = PatternUtil.between(respone,"/orderquery\\?orderid=","\"",1);
        List<String>  orderids = PatternUtil.between(respone,"/merchant/order/fetchcard\\?id=","\">",1);
        for (int i=0;i<orderIdcs.size();i++) {
            if (redisService.get(orderIdcs.get(i))==null) {
                redisService.set(orderIdcs.get(i),orderids.get(i),24*60*60L);
            }
        }
        String id = (String) redisService.get(orderid);
        if (StringUtils.isEmpty(id)) {
            return "<div align=\"center\"<h3>请确认该订单是否已经支付</h3></div>";
        }
        String responeCar = HttpClientUtil.get("https://www.csfaka.com/merchant/order/fetchcard?id="+id,head);
        String content = responeCar.substring(responeCar.indexOf("<div class=\"form-group \">"),responeCar.indexOf("</form>"));
        content = "<div align=\"center\">"+content+"</div>";
        redisService.set(orderid+"data",content,24*60*60L);
        return content;
    }

    
    /** 
     * @Description:  
     * @Param: 重载卡密
     * @return: java.util.Map<java.lang.String,java.lang.Object> 
     * @Author: 邓太阳 
     * @Date: 2020-06-27 10:22
     */ 
    @GetMapping("/reloadSC")
    public Map<String,Object> reloadSC() throws IOException {
        Map<String,Object> data = new HashMap<>();
        Map<String,String> head = new HashMap<>();
        if (cookie==null||"".equalsIgnoreCase(cookie)) {
            data.put("code","0");
            data.put("message","请先设置cookie");
            return data;
        }
        head.put("cookie",cookie);
        String respone = HttpClientUtil.get("https://www.csfaka.com/merchant/agent/myorder",head);
        List<String> orderIdcs = PatternUtil.between(respone,"/orderquery\\?orderid=","\"",1);
        List<String>  orderids = PatternUtil.between(respone,"/merchant/order/fetchcard\\?id=","\">",1);
        Boolean ishit =false;
        for (int i=0;i<1;i++) {
            String responeCar = HttpClientUtil.get("https://www.csfaka.com/merchant/order/fetchcard?id="+orderids.get(i),head);
            String content = responeCar.substring(responeCar.indexOf("<div class=\"form-group \">"),responeCar.indexOf("</form>"));
            List<String> phones = PatternUtil.getyzmS(content,11);
            List<String> coms = PatternUtil.between(content,"COM","\t\t\t\t\t\t\t<",1);

            for (int j=0;j<coms.size();j++){
                ishit = true;
                SmsEnt smsEnt = smsDao.getByKey(phones.get(j));
                if (smsEnt!=null) {
                    continue;
                }else {
                    smsEnt = new SmsEnt();
                    smsEnt.setKeys("COM"+coms.get(j));
                    smsEnt.setPhone(phones.get(j));
                    smsEnt.setStatus("R");
                    data.put("code",smsDao.addSMS(smsEnt));
                    return data;
                }
            }


        }
        if (ishit) {
            data.put("message","已经加载过了");
            data.put("code","0");
            return data;
        }
        data.put("message","请先设置cookie");
        data.put("code","0");
        return data;
    }


    public static void main(String[] args) throws IOException {

    String a = "CS200501032002899510/l/d9410de3e0cd8c32927bb2c6c37685f1";

        System.out.println(a.substring(4,10));
        Calendar now = Calendar.getInstance();

        String m = ((now.get(Calendar.MONTH) + 1)<=9?"0"+(now.get(Calendar.MONTH) + 1):(now.get(Calendar.MONTH) + 1)+"");
        String d = (now.get(Calendar.DAY_OF_MONTH)<=9?"0"+now.get(Calendar.DAY_OF_MONTH):now.get(Calendar.DAY_OF_MONTH)+"");
        String h = (now.get(Calendar.HOUR_OF_DAY)<=9?"0"+now.get(Calendar.HOUR_OF_DAY):now.get(Calendar.HOUR_OF_DAY)+"");
        String nows = m+d+h;


    }



}
