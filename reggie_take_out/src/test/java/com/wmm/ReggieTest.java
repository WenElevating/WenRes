package com.wmm;

import com.wmm.utils.EmailUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
public class ReggieTest {

    @Autowired
    private EmailUtil sendEmailUtil;
    @Test
    public void tes01(){
        sendEmailUtil.sendEmail("2830089732@qq.com");
    }
}
