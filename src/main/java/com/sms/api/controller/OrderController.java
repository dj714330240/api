package com.sms.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sms.api.controller.SMSController.*;

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

    public static Double onePrice=0.0;


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


    public static void main(String[] args) throws IOException, InterruptedException {
//getCar("123456");
        while (true){
            Calendar now = Calendar.getInstance();
            String h = (now.get(Calendar.HOUR_OF_DAY)<=9?"0"+now.get(Calendar.HOUR_OF_DAY):now.get(Calendar.HOUR_OF_DAY)+"");
            System.out.println(h);
            if (Integer.parseInt(h)>8) {
                Map<String,Object> data = new HashMap<>();
                String[] gets = getList(new File("mhh.txt"));
                for (String s:gets){
                    getCar(s.trim());
                    Thread.sleep(800);
                }
            }else {
                Thread.sleep(1000*60*60);
            }

        }


    }



    public static void getCar(String phone) throws IOException, InterruptedException {
        Map<String,String> head = new HashMap<>();
        head.put("cookie","UM_distinctid=1748a8c6c15369-0c48b44b7e561f-316d7005-384000-1748a8c6c16a24; sd0406650=snp2j2psram77co8c8295ca6s0; CNZZDATA1279029842=1310953567-1600249785-%7C1600249785");
        String url = "http://www.5ifk.net";
        String queryRes = HttpClientUtil.get(url+"/orderquery?orderid="+phone+"&chkcode="+getCk()+"&querytype=3",head);
//        System.out.println(queryRes);
        List<String> orderIds = PatternUtil.between(queryRes,"/orderquery/orderid/","\"",1);
        String title = PatternUtil.between(queryRes,"<p>","</p>");
        for (int i=0;i<orderIds.size();i++){
            Thread.sleep(200);
            String times = orderIds.get(i).substring(5,11);
            if(!getRun(times,"快手")) {
                continue;
            }
            String res = HttpClientUtil.get(url+"/orderquery/orderid/"+orderIds.get(i),head);
            String qq = PatternUtil.between(res,"\">","</a></b>");

            String sfk  = PatternUtil.between(res,"实付款：<b>","</b></p>");
            String orderTime = PatternUtil.between(res,"订单日期：","</h3>");
            String id = orderIds.get(i).split("/")[0];
            String toekn = PatternUtil.between(res,"token: \"","\"");
            if (toekn =="") {
                continue;
            }

            String key=HttpClientUtil.get(url+"/checkgoods?orderid="+id+"&token="+toekn+"&t="+System.currentTimeMillis(),head);
             String time = PatternUtil.between(res,"订单日期：","</h5>");
            Map<String,Object> resData = (Map<String, Object>) JSONObject.parse(key);
            if (resData.get("quantity")==null || StringUtils.isEmpty(sfk)) {
                onePrice=0.0;
            }else {
                onePrice=(Double.parseDouble(sfk)/Integer.parseInt(resData.get("quantity").toString()));
            }

            addFile("mhhcar.txt",orderTime+"--"+onePrice+"--"+qq+"-----"+(extract((resData.get("msg")!=null?resData.get("msg").toString():""),phone,orderTime,qq+"--"+onePrice))+"\n");
            System.err.println(resData);
        }


    }



    public static Pattern pattern = Pattern.compile("COM(.+)<a.+卡密：(.+)<a");

    public static List<String> extract(String html,String qq,String time,String title) {
        html = html.replace("\n", "");
        String[] card_items = html.split("卡号：");
        List<String> cardList = new ArrayList<>();
        for (String card_item : card_items) {
            Matcher matcher = pattern.matcher(card_item);
            if (matcher.find()) {
                String cardNumber = matcher.group(1);
                String cardPass = matcher.group(2);
                cardList.add("COM"+cardNumber + "--" + cardPass);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String s = HttpClientUtil.get("http://account.ttgz.top/ks/addAccount?key=COM"+cardNumber+"&mobile="+cardPass+"&qq="+qq+"&time="+ URLEncoder.encode( time, "UTF-8" )+"&title="+URLEncoder.encode( title, "UTF-8" ));
                            System.out.println(s);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
        return cardList;
    }




}
