package com.sms.api.model;

import lombok.Data;

/**
 * @program: FEBS-Shiro
 * @description: 消息模板
 * @author: 邓太阳
 * @create: 2020-06-18 16:09
 **/
@Data
public class SMSBody {
    private String group_id;
    private String message;
}
