package com.example.demo.util;

/**
 * 常量
 */
public  class ConstantUtil {

    //国家
    public  static String CHINA = "http://www.weather.com.cn/data/city3jdata/china.html";
    //省份
    public static String PROVINCE = "http://www.weather.com.cn/data/city3jdata/provshi/PROVINCE.html";
    //城市
    public static String CITY  = "http://www.weather.com.cn/data/city3jdata/station/CITY.html";
    //县区
    public static  String COUNTY  = "http://www.weather.com.cn/data/sk/COUNTY.html";

    //数据问题
    //成功
    public static Integer RES_CODE_0=0;

    //数据不完整
    public static Integer RES_CODE_1=1;
    //数据非法
    public static Integer RES_CODE_2=2;
    //连接失败
    public static Integer RES_CODE_3=3;
    //tps超过100次/m
    public static Integer RES_CODE_4=4;

    //TPS 最大
    public static Integer MAX = 100;
    //尝试次数
    public static Integer MAXTRY = 3;



}
