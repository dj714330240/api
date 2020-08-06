package com.sms.api.dao;

import com.sms.api.model.SmsEnt;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


/**
 * @program: api
 * @description:
 * @author: 邓太阳
 * @create: 2020-06-24 22:24
 **/
@Mapper
public interface SMSDao {

    @Select("select * from ks_sms where phone = #{phone} and status = #{status}")
    SmsEnt getByPhone(SmsEnt smsEnt);

    @Select("select * from ks_sms where phone = #{phone}")
    SmsEnt getByKey(String phone);

    @Insert("insert into ks_sms (`keys`,phone,create_time,modify_time,status) VALUES (#{keys},#{phone},now(),now(),#{status})")
    Integer addSMS(SmsEnt smsEnt);

    /*** 
     * @Description: 注册完更新密码 
     * @Param: [smsEnt] 
     * @return: java.lang.Iks_smsnteger
     * @Author: 邓太阳 
     * @Date: 2020-06-24 23:50
     */ 
    @Update("update ks_sms set pwd = #{pwd},status = #{status},modify_time = now() where phone = #{phone}")
    Integer updateSMS(SmsEnt smsEnt);


    @Update("update ks_sms set status = #{status},modify_time = now() where phone = #{phone}")
    Integer updateStatus(SmsEnt smsEnt);



    @Update("update ks_sms set status = #{status},modify_time = now() where phone = #{phone}")
    Integer setStatus(SmsEnt smsEnt);

    /*** 
     * @Description: 获取状态密码
     * @Param: [status] 
     * @return: java.util.List<com.sms.api.model.SmsEnt> 
     * @Author: 邓太阳 
     * @Date: 2020-06-24 23:51
     */ 
    @Select("select * from ks_sms where status = #{status} order by create_time desc ")
    List<SmsEnt> getListByStatus(String status);

}
