package com.sms.api;

import com.sms.api.dao.SMSDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiApplicationTests {

    @Autowired
    SMSDao smsDao;

//    @Test
//    void contextLoads() {
//        System.out.println(smsDao.getByPhone("18879713881"));
//    }

}
