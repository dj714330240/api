package com.sms.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: api
 * @description: 正则工具类
 * @create: 2020-06-20 22:45
 **/
public class PatternUtil {

    private static String rxkey = "COM(.*?)\\s";

    private static String rgex ="(.*?)";

    public static String getSubUtilSimple(String soap,String rgex){
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);
        while(m.find()){
            return m.group(1);
        }
        return "";
    }

    public static String getyzm(String body, int YZMLENGTH) {
        // 首先([a-zA-Z0-9]{YZMLENGTH})是得到一个连续的六位数字字母组合
        // (?<![a-zA-Z0-9])负向断言([0-9]{YZMLENGTH})前面不能有数字
        // (?![a-zA-Z0-9])断言([0-9]{YZMLENGTH})后面不能有数字出现
        Pattern p = Pattern.compile("(?<!['*'])+(?<![0-9])([0-9]{"+YZMLENGTH+"})(?![0-9])");

        Matcher m = p.matcher(body);
        if (m.find()) {
            return m.group(0);
        }
        return null;
    }


    public static List<String> getyzmS(String body, int YZMLENGTH) {
        // 首先([a-zA-Z0-9]{YZMLENGTH})是得到一个连续的六位数字字母组合
        // (?<![a-zA-Z0-9])负向断言([0-9]{YZMLENGTH})前面不能有数字
        // (?![a-zA-Z0-9])断言([0-9]{YZMLENGTH})后面不能有数字出现
        Pattern p = Pattern.compile("(?<!['*'])+(?<![0-9])([0-9]{"+YZMLENGTH+"})(?![0-9])");

        Matcher m = p.matcher(body);
        List<String> ids = new ArrayList<>();
        while (m.find()) {
            ids.add(m.group(0));
        }
        return ids;
    }



    /***
     * @Description:
     * @Param: [str, start, end]
     * @return: java.lang.String
     * @Date: 2020-06-20 22:50
     */
    public static String between(String str,String start,String end){
        return getSubUtilSimple(str,(start+rgex+end));
    }



    /***
     * @Description:
     * @Param: [str, start, end]
     * @return: java.lang.String
     * @Date: 2020-06-20 22:50
     */
    public static List<String> between(String str, String start, String end,int i){
        return getSubUtilSimple(str,(start+rgex+end),i);
    }

    public static List<String> getSubUtilSimple(String soap,String rgex,int i){
        List<String> list = new ArrayList<>();
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);
        while(m.find()){
            list.add(m.group(1));
        }
        return list;
    }

    public static Pattern pattern = Pattern.compile("<span class=\"card_number\">(.+)</span>.+<span class=\"card_pass\">(.+)</span>");

    public static List<String> extract(String html) {
        html = html.replace("\n", "");
        String[] card_items = html.split("card_item");
        List<String> cardList = new ArrayList<>();
        for (String card_item : card_items) {
            Matcher matcher = pattern.matcher(card_item);
            if (matcher.find()) {
                String cardNumber = matcher.group(1);
                String cardPass = matcher.group(2);
                cardList.add(cardNumber + "--" + cardPass);
            }
        }
        return cardList;
    }

    public static Pattern patternTime = Pattern.compile("");

}
