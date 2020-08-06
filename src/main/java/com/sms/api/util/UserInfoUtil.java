package com.sms.api.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

/**
 * @program: api
 * @description: 用户资料随机
 * @author: 邓太阳
 * @create: 2020-06-24 11:10
 **/
@Component
public class UserInfoUtil {

    public  List<String> heads = new ArrayList<>();

    public  List<String> name = new ArrayList<>();

    @Value("${sys.head.dir}")
    private String headPath;

    @Value("${sys.name.dir}")
    private String namePath;
//    t:zoneid: zone
//    t:submit: ["submit_0","submit_0"]
//    t:formdata: A0ZMbLbynQPYUiTMzm/n0vCIdM4=:H4sIAAAAAAAAAJWQsUoEMRCGx4WDk1MERUHBTtuchddo4yEIwiLCYi3Z7NxeZDeJyZy7Nla+xDU+gVjpE1xh5zv4ADYWVhZudk8QRMVu+PMn35e5fYFWsQ5roU6l2jFDrXAgMUsYYUn1BM5CT9uUccPFEBlxg47sZY8JbTGTMYu5Q9aPq5ALOvB3NiKkkdk8mXSelx/fA5gJoSO0IquzI54jwWJ4xi94N+Mq7UZkpUp3S0Mw66n1C3879f/rdGy1QOeiUZxL56RWk7tke/B28xQAlKZYgaUpkDtXaJtMSedwBUAw/xk3ej+0fblVLMBcc+g8i7zt3q+2Quem+qUix2o9+i47jl5XH+6v9wMIQmiLTFbtw8Tz/HIxw7wK/HLryC+z3dBPt76MH16mqiLvAQAA
//    textField: 18879713881
//    passwordField: dj714330240

    public static void main(String[] args) throws IOException {

        String cookie = "";
        String textField ="18879713881";
        String passwordField ="dj714330240";
        String formdata = "";
        String url = "http://zodlease.sap1200.com/login.form";

        
        //加载登陆页面

        Map<String,String> data = HttpClientUtil.getMap("http://zodlease.sap1200.com/login");
        cookie = data.get("cookie");
        Document document = Jsoup.parse(data.get("data"));

        Elements postList = document.getElementsByTag("input");

        for (Element element:postList){
            if (element.getElementsByAttribute("value").val().length()>100) {
                formdata = element.getElementsByAttribute("value").val();
            }
        }
        //开始登陆

        Map<String,String> headers = new HashMap<>();
        headers.put("Cookie",cookie);

        Map<String,String> parm = new HashMap<>();
        parm.put("t:submit","[\"submit_0\",\"submit_0\"]");
        parm.put("t:formdata",formdata);
        parm.put("textField",textField);
        parm.put("passwordField",passwordField);
        cookie = HttpClientUtil.postMap(url,headers,parm).get("cookie");
        headers.put("Cookie",cookie);
        String contetn = HttpClientUtil.get("http://zodlease.sap1200.com/user",headers);
        document = Jsoup.parse(contetn);
        Elements elements = document.getElementsByTag("tr");
        for (Element element:elements){
            for (Element e:element.children()) {
                    System.out.print(e.text()+"\t");
            }
            System.out.println();
        }

    }

    public  String getUserName() throws IOException {
        if (name!=null && name.size()>0) {
            Random random = new Random();
            int i = random.nextInt(name.size());
            return name.get(i);

        }else {
            initName();
            return getUserName();
        }

    }

    public  String getUserHead() throws IOException {
        if (heads!=null && heads.size()>0) {
            Random random = new Random();
            int i = random.nextInt(heads.size());
            return heads.get(i);

        }else {
            initHead();
            return getUserHead();
        }
    }


    public  void initHead(){
        File file = new File(headPath);
        File[] files = file.listFiles();
        for (File img:files) {
            heads.add(img.getName());
        }
    }


    public  void initName() throws IOException {
        InputStreamReader read = new InputStreamReader(
                new FileInputStream(new File(namePath)),"utf-8");//考虑到编码格式
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineTxt = null;
        while((lineTxt = bufferedReader.readLine()) != null){
            name.add(lineTxt);
        }
        read.close();
    }




}
