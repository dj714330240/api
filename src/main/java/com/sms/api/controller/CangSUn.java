package com.sms.api.controller;

import com.sms.api.util.HttpClientUtil;
import com.sms.api.util.PatternUtil;

import java.io.IOException;
import java.util.List;

/**
 * @program: api
 * @description:
 * @author: 邓太阳
 * @create: 2020-07-25 13:16
 **/
public class CangSUn {

    public static void main(String[] args) throws IOException {

        String a = "{\"msg\":\"<article class=\\\"card_item\\\"><p>卡号：<span class=\\\"card_number\\\">COM10<\\/span><a href=\\\"javascript:;\\\" class=\\\"btn btn-purple waves-effect waves-light clipboard\\\" data-clipboard-text=\\\"COM10\\\">复制<\\/a><\\/p><p>卡密：<span class=\\\"card_pass\\\">13416146994<\\/span><a href=\\\"javascript:;\\\" class=\\\"btn btn-purple waves-effect waves-light clipboard\\\" data-clipboard-text=\\\"13416146994\\\">复制<\\/a><\\/p><\\/article><article class=\\\"card_item\\\"><p>卡号：<span class=\\\"card_number\\\">COM8<\\/span><a href=\\\"javascript:;\\\" class=\\\"btn btn-purple waves-effect waves-light clipboard\\\" data-clipboard-text=\\\"COM8\\\">复制<\\/a><\\/p><p>卡密：<span class=\\\"card_pass\\\">17827067823<\\/span><a href=\\\"javascript:;\\\" class=\\\"btn btn-purple waves-effect waves-light clipboard\\\" data-clipboard-text=\\\"17827067823\\\">复制<\\/a><\\/p><\\/article><article class=\\\"card_item\\\"><p>卡号：<span class=\\\"card_number\\\">COM7<\\/span><a href=\\\"javascript:;\\\" class=\\\"btn btn-purple waves-effect waves-light clipboard\\\" data-clipboard-text=\\\"COM7\\\">复制<\\/a><\\/p><p>卡密：<span class=\\\"card_pass\\\">13427573534<\\/span><a href=\\\"javascript:;\\\" class=\\\"btn btn-purple waves-effect waves-light clipboard\\\" data-clipboard-text=\\\"13427573534\\\">复制<\\/a><\\/p><\\/article><p>使用说明：售后老号和不来码<\\/p><p><a id=\\\"dumpCardsBtn\\\" style=\\\"padding:5px 8px;background-color:#678cf7;color:white\\\" href=\\\"\\/index\\/order\\/dumpCards?trade_no=CS2007250353481516298\\\" target=\\\"_blank\\\">导出卡密<\\/a><\\/p><p><a href=\\\"javascript:;\\\" class=\\\"copyCardsBtn\\\" style=\\\"padding:5px 8px;background-color:#678cf7;color:white\\\"  data-clipboard-action=\\\"copy\\\" data-clipboard-target=\\\"#copyTarget\\\" >一键复制全部<\\/a><\\/p><div id=\\\"copyTarget\\\" style=\\\"opacity: 0;float: left; width: 0.1px;height: 0.1px;position: relative;overflow: hidden;\\\"><\\/div>\",\"quantity\":3,\"status\":1}";

        String as = PatternUtil.between(a,"卡",":");

    }

}
