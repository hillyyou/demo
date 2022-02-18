package com.example.demo;

import com.example.demo.service.TempService;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.RateLimiter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Autowired
    private TempService tempService;

    @Autowired
    private RestTemplate restTemplate;


    @Test
    public void contextLoads() {

    }


    @Test
    public void testA(){
        int a = tempService.getTemperature("","","").get();
        Assert.assertEquals(a,1);
    }

    @Test
    public void testB(){
        int a = tempService.getTemperature("江南","苏州","吴中").get();
        Assert.assertEquals(a,2);
    }

    /**
     * 正常处理
     */
    @Test
    public void testC(){
        int a = tempService.getTemperature("江苏","苏州","吴中").get();
        Assert.assertEquals(a,23);
    }

    /**
     * api 调用重试 3次
     *
     */
    @Test
    public void testD(){
        String uri = "http://localhost:8080/hello";
        restTemplate.getForEntity(uri,String.class);
        Assert.assertEquals(23,23);
    }

    /**
     * 限流100次
     */
    @Test
    public  void testF(){
        String uri = "http://localhost:8080/hello";
        //构建100个调用，同时发出
        ExecutorService pool = Executors.newFixedThreadPool(120);
            restTemplate.getForEntity(uri,String.class);


    }

}
