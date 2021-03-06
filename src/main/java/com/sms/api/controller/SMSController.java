package com.sms.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.sms.api.dao.SMSDao;
import com.sms.api.model.SmsEnt;
import com.sms.api.util.HttpClientUtil;
import com.sms.api.util.PatternUtil;
import com.sms.api.util.UserInfoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.*;

/**
 * @program: api
 * @description:
 * @author: 邓太阳
 * @create: 2020-06-24 23:19
 **/
//@Api(value = "SMSController", description = "手机号 API", tags = "手机号支持-API")
@RestController
@RequestMapping("/sms/phone")
public class SMSController {

    @Autowired
    SMSDao smsDao;



    @Autowired
    UserInfoUtil userInfoUtil;

    @GetMapping("/get")
    public Map<String,Object> getPhone(String status) throws IOException {
        Map<String,Object> data = new HashMap<>();

        data.put("data",smsDao.getListByStatus(status));
        data.put("head", userInfoUtil.getUserHead());
        data.put("name", userInfoUtil.getUserName());
        return data;
    }

    @GetMapping("/add")
    public Map<String,Object> add(String phone,String key) throws IOException {
        Map<String,Object> data = new HashMap<>();
        SmsEnt smsEnt = new SmsEnt();
        smsEnt.setPhone(phone);
        smsEnt.setStatus("R");
        smsEnt.setKeys(key);
        data.put("code",smsDao.addSMS(smsEnt));
        return data;
    }

    /*** 
     * @Description: 锁号
     * @Param: [phone] 
     * @return: java.util.Map<java.lang.String,java.lang.Object> 
     * @Author: 邓太阳 
     * @Date: 2020-06-25 00:30
     */ 
    @GetMapping("/lock")
    public Map<String,Object> lock(String phone){
        Map<String,Object> data = new HashMap<>();
        SmsEnt smsEnt = new SmsEnt();
        smsEnt.setPhone(phone);
        smsEnt.setStatus("I");
        smsDao.setStatus(smsEnt);
        data.put("data","succeed");
        return data;
    }

    @GetMapping("/update")
    public Map<String,Object> updateUser(String phone,String pwd){
        Map<String,Object> data = new HashMap<>();
        SmsEnt smsEnt = new SmsEnt();
        smsEnt.setPhone(phone);
        smsEnt.setStatus("Y");
        smsEnt.setPwd(pwd);
        data.put("code",smsDao.updateSMS(smsEnt));
        return data;
    }


    @GetMapping("/updateStatus")
    public Map<String,Object> updateStatus(String phone,String status){
        Map<String,Object> data = new HashMap<>();
        SmsEnt smsEnt = new SmsEnt();
        smsEnt.setPhone(phone);
        smsEnt.setStatus(status);

        data.put("code",smsDao.updateStatus(smsEnt));
        return data;
    }

    /*** 
     * @Description: 加载资源
     * @Param: [] 
     * @return: java.util.Map<java.lang.String,java.lang.Object> 
     * @Author: 邓太阳 
     * @Date: 2020-06-27 01:48
     */ 
    @GetMapping("/reload")
    public Map<String,Object> reload() throws IOException {
        Map<String,Object> data = new HashMap<>();
        String queryRes = HttpClientUtil.get("http://mhhfk.com/orderquery?orderid=104561&querytype=3");
        List<String> orderIds = PatternUtil.between(queryRes,"/orderquery/orderid/","\"",1);
        Boolean isHist =false;
        for (int i=0;i<1;i++){
            String res = HttpClientUtil.get("http://mhhfk.com/orderquery/orderid/"+orderIds.get(i));
            String id = orderIds.get(i).split("/")[0];
            String toekn = PatternUtil.between(res,"token: \"","\"");
            String key=HttpClientUtil.get("http://mhhfk.com/checkgoods?orderid="+id+"&token="+toekn);
            System.out.println(key);
            Map<String,Object> resData = (Map<String, Object>) JSONObject.parse(key);
            if (resData.get("status")!=null) {
                List<String> keys = PatternUtil.between(resData.get("msg").toString(),"卡号：","<",1);
                List<String> phones = PatternUtil.between(resData.get("msg").toString(),"卡密：","<",1);
                for (int j=0;j<keys.size();j++) {
                    isHist=true;
                    SmsEnt smsEnt = smsDao.getByKey(phones.get(j));
                    if (smsEnt!=null) {
                        continue;
                    }else {
                        smsEnt = new SmsEnt();
                        smsEnt.setKeys(keys.get(j));
                        smsEnt.setPhone(phones.get(j));
                        smsEnt.setStatus("R");
                        smsDao.addSMS(smsEnt);
                        data.put("code",smsDao.addSMS(smsEnt));
                        return data;
                    }
                }
            }
        }
        if (isHist) {
            data.put("message","已经加载过了1");
            data.put("code","0");
            return data;
        }
        data.put("message","请先设置cookie");
        data.put("code","0");
        return data;
    }
    public static String[] getList(File file){
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s);
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString().split(",");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
//            getCar("616578536");
        while (true){
            Calendar now = Calendar.getInstance();
            String h = (now.get(Calendar.HOUR_OF_DAY)<=9?"0"+now.get(Calendar.HOUR_OF_DAY):now.get(Calendar.HOUR_OF_DAY)+"");
            System.out.println(h);
            if (Integer.parseInt(h)>8) {
                Map<String,Object> data = new HashMap<>();
                String[] gets = getList(new File("all.txt"));
                for (String s:gets){
                    getCar(s.trim());
                    Thread.sleep(500);
                }
            }else {
                Thread.sleep(1000*60*60);
            }

        }


    }

    public static String getCk(){
        String code = UUID.randomUUID().toString().substring(0,4);
        return code;

    }



    public static void getCar(String phone) throws IOException {
        Calendar now = Calendar.getInstance();
        String m = ((now.get(Calendar.MONTH) + 1)<=9?"0"+(now.get(Calendar.MONTH) + 1):(now.get(Calendar.MONTH) + 1)+"");
        String d = (now.get(Calendar.DAY_OF_MONTH)<=9?"0"+now.get(Calendar.DAY_OF_MONTH):now.get(Calendar.DAY_OF_MONTH)+"");
//        String url = "http://mhhfk.com";
        String url = "https://www.csfaka.com";
        Map<String,String> head = new HashMap<>();
        head.put("cookie","fpv=b38fe3d7f10d6310b307171f0184f91a; waf_cookie=5ba40cc6-300e-400e62eb4fe999a03ff7e5bdc241f7639a54; se9b4902c=t70316rtaeslppeo2l6u3ofi76; s73d83f01=a1fe42a5moj0sdntjb2g3c9kc0; s17f4db19=pvebtp20ccqomucc73sjuqvsv1; __51cke__=; __tins__20416941=%7B%22sid%22%3A%201598858512537%2C%20%22vd%22%3A%203%2C%20%22expires%22%3A%201598860530364%7D; __51laig__=3");
        String queryRes = HttpClientUtil.get(url+"/orderquery?orderid="+phone+"&chkcode="+getCk()+"&querytype=3",head);
        if (queryRes==null) {
            return;
        }
        String time = PatternUtil.between(queryRes,"target=\"_blank\">","</a></h2>");
        List<String> orderIds = PatternUtil.between(queryRes,"/orderquery/orderid/","\"",1);
        String title = PatternUtil.between(queryRes,"<p>","</p>");
        System.err.println(time+"*******"+title);
        if (orderIds == null || orderIds.size()<=0) {
            return;
        }
        addFile("qq",phone+",");
        Boolean isHist =false;
        for (int i=0;i<orderIds.size();i++){
            String times = orderIds.get(i).substring(4,10);

            if (!getRun(times,title) || !orderIds.get(i).contains((m+d))) {
                continue;
            }

            addFile("phone.txt",phone+",");
            String res = HttpClientUtil.get(url+"/orderquery/orderid/"+orderIds.get(i),head);
//            String res = HttpClientUtil.get("https://www.csfaka.com/orderquery/orderid/CS2008132218522351363/l/16d0886adb8743317905a835d496d2d7",head);
            if (res==null) {
                continue;
            }
//            System.out.println(res);
//            System.err.println("1111"+PatternUtil.between(res,"wpa.qq.com/msgrd?v=3&amp;uin=","&amp;site=qq&amp;menu=yes"));
            String id = orderIds.get(i).split("/")[0];
            String toekn = PatternUtil.between(res,"token: \"","\"");
            System.out.println("--------------"+phone+"--------------");
            if (toekn =="") {
                continue;
            }
            head.put("Host","www.csfaka.com");
            head.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36");
            head.put("cookie","fpv=b38fe3d7f10d6310b307171f0184f91a; waf_cookie=5ba40cc6-300e-400e62eb4fe999a03ff7e5bdc241f7639a54; se9b4902c=t70316rtaeslppeo2l6u3ofi76; s73d83f01=a1fe42a5moj0sdntjb2g3c9kc0; s17f4db19=pvebtp20ccqomucc73sjuqvsv1; __51cke__=; __tins__20416941=%7B%22sid%22%3A%201598858512537%2C%20%22vd%22%3A%203%2C%20%22expires%22%3A%201598860530364%7D; __51laig__=3");
            head.put("Referer",url+"/orderquery?orderid="+phone+"&chkcode="+getCk()+"&querytype=3");
            String key=HttpClientUtil.get(url+"/checkgoods?orderid="+id+"&token="+toekn,head);
            System.out.println(key);
            Map<String,Object> resData = (Map<String, Object>) JSONObject.parse(key);
            if (resData.get("status")!=null) {
                List<String> keys = PatternUtil.between(resData.get("msg").toString(),"卡号：","<",1);
                List<String> phones = PatternUtil.between(resData.get("msg").toString(),"卡密：","<",1);

                if(!url.contains("mhhfk")){
                    time = PatternUtil.between(res,"订单日期：","</h5>");
                    addFile("card.txt",time+"---"+title+"--"+PatternUtil.extract(resData.get("msg").toString(),time,title,phone)+"--"+phone+"\n");
                    return;
                }

//                for (int j=0;j<keys.size();j++) {
//                    isHist=true;
//                    if (url.contains("mhhfk")) {
//                        addFile("card.txt",time+"---"+keys.get(j)+"--"+phones.get(j)+"--"+phone+"\n");
//                    }else {
//
//                        time = PatternUtil.between(res,"订单日期：","</h5>");
//
//                        addFile("card.txt",time+"---"+PatternUtil.extract(resData.get("msg").toString())+"--"+phone+"\n");
//                    }
//                }
            }
        }
    }

    public static Boolean getRun(String time,String title){
        boolean titles = false;

        if (title.contains("快手")){
            titles = true;
        }else if (title.contains("ks")){
            titles=  true;
        }else if (title.contains("快")){
            titles=  true;
        }else if (title.contains("手")){
            titles= true;
        }else if (title.contains("筷")){
            titles= true;
        }else if (title.contains("K")){
            titles= true;
        }else if (title.contains("S")){
            titles= true;
        }else if (title.contains("s")){
            titles= true;
        }else if (title.contains("k")){
            titles= true;
        }
//        time = time.substring(4,10);
        if (titles){
            Calendar now = Calendar.getInstance();
            String m = ((now.get(Calendar.MONTH) + 1)<=9?"0"+(now.get(Calendar.MONTH) + 1):(now.get(Calendar.MONTH) + 1)+"");
            String d = (now.get(Calendar.DAY_OF_MONTH)<=9?"0"+now.get(Calendar.DAY_OF_MONTH):now.get(Calendar.DAY_OF_MONTH)+"");
            String h = (now.get(Calendar.HOUR_OF_DAY)<=9?"0"+now.get(Calendar.HOUR_OF_DAY):now.get(Calendar.HOUR_OF_DAY)+"");
            String nows = m+d+h;
            if ((Integer.parseInt(nows)-Integer.parseInt((time))<=5)){
                return true;
            }
        }

            return false;
    }


    public static void addFile(String name,String content) {
        FileWriter fw = null;
        try {
            //如果文件存在，则追加内容；如果文件不存在，则创建文件
            File f=new File(name);
            fw = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.print(content);
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
