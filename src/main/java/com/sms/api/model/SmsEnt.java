package com.sms.api.model;

import lombok.Data;

import java.util.Date;

/**
 * @program: api
 * @description:
 * @author: 邓太阳
 * @create: 2020-06-24 22:43
 **/
@Data
public class SmsEnt {

    private Long id;

    private String keys;

    private String phone;

    private String pwd;

    private Date createTime;

    private Date modifyTime;

    private String status;

}
