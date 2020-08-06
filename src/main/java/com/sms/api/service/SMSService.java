package com.sms.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SMSService {

    private static final Logger log = LoggerFactory.getLogger(SMSService.class);

    private Map<String, String> smsMap = new HashMap<>();

    private Pattern comPattern = Pattern.compile("COM\\d+");

    public String receive(String content) {
//        if (!content.contains("快手")) {
//            return null;
//        }
        log.info(content);
        Matcher comMatcher = comPattern.matcher(content);
        if (comMatcher.find()) {
            String com = comMatcher.group(0);
            smsMap.put(com, content);
            return com+"#"+content;
        }else {
            return "";
        }
    }

    public String get(String com) {
        return smsMap.get(com);
    }

    public static void main(String[] args) throws IOException {

//        List<String> list = new ArrayList<>();
//        String path = "/Users/dengtaiyang/tmp/imgs";
//        File file = new File(path);
//        File[] files = file.listFiles();
//        for (File img:files) {
//            list.add(img.getName());
//            System.out.println(img.getName());
//        }
//        System.out.println(list.size());

        InputStreamReader read = new InputStreamReader(
                new FileInputStream(new File("/Users/dengtaiyang/tmp/1.txt")),"utf-8");//考虑到编码格式
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineTxt = null;
        while((lineTxt = bufferedReader.readLine()) != null){
            System.out.println(lineTxt);
        }
        read.close();



////
//
////        System.out.println(HttpClientUtil.get("https://www.csfaka.com/orderquery?orderid=CS200620225825266610&chkcode=APKK&querytype=2"));
//
//
//        Map<String,String> head = new HashMap<>();
//        head.put("cookie","waf_cookie=85d27008-bc13-4531933869a1b1acc4db9eddca92c799e7e6; s17f4db19=k5esjr7jnfqbntu6un54nusbu3; __51cke__=; freeze_money_tip=1; has_merchant_backend_one=1; __tins__20416941=%7B%22sid%22%3A%201592665165273%2C%20%22vd%22%3A%2023%2C%20%22expires%22%3A%201592667948513%7D; __51laig__=26");
//
//        String respone = HttpClientUtil.get("https://www.csfaka.com/merchant/order/fetchcard?id=3484253",head);
//        System.out.println(respone);
//
//        System.out.println(PatternUtil.between(respone,"<div class=\"form-group \">","</form>",1));
//        System.out.println(respone.substring(respone.indexOf("<div class=\"form-group \">"),respone.indexOf("</form>")));
    }
}
