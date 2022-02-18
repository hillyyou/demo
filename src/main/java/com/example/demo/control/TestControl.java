package com.example.demo.control;



import com.example.demo.service.TempService;
import com.example.demo.util.ConstantUtil;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;


import java.util.Optional;
import java.util.concurrent.CountDownLatch;


@RestController
public class TestControl {
    @Autowired
    private TempService tempService;
    //令牌桶处理
    RateLimiter rateLimiter = RateLimiter.create(100);//qps

    @RequestMapping("/hello")
    public Integer hello(){
        if (rateLimiter.tryAcquire()) {//获取一个令牌
            System.out.println("允许通过，进行访问");
            Optional<Integer> a = tempService.getTemperature("江苏","苏州","吴中");
            //每秒限流100次
            System.out.println(a.get());
            return a.get();
        }else {
            System.out.println("稍后再试");
            return ConstantUtil.RES_CODE_3;
        }
    }


    public static void main(String[] args) throws Exception{
        RateLimiter rateLimiter = RateLimiter.create(10);
        CountDownLatch latch = new CountDownLatch(1);
        //构建100个调用，同时发出
        for (int i = 0; i < 105; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //预备
                        latch.await();
                        //检查频率
                        // Preconditions.checkState(rateLimiter.acquire(), "令牌不足则等待");
                        Preconditions.checkState(rateLimiter.tryAcquire(), "令牌不足则立即返回");

                        //频率检查通过，执行业务代码
                        System.out.println("业务执行中...");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        System.out.println("wait...");
        Thread.sleep(5000);
        //go!
        latch.countDown();
    }
}
