package com.example.demo.service;


import cn.hutool.json.JSONObject;
import com.example.demo.util.ConstantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class TempService {

    @Autowired
    private RestTemplate restTemplate;

    public static Map<String,Object> provinceMap = new HashMap<>();

    /**
     *获取省份数据
     * @throws Exception
     */
    public   void initMap() {
        if(provinceMap.size()==0) {
            provinceMap  = getChina(1,"1");
        }
    }


    /**
     * 获取天气Api 数据
     * @param type
     * @param param
     * @return
     */
    public Map<String, Object> getChina(Integer type, String param){
        String uri ="";
        if(type.intValue()==1){
            uri = ConstantUtil.CHINA;
        }
        else if(type.intValue()==2){
            uri = ConstantUtil.PROVINCE.replace("PROVINCE",param);
        }
        else if(type.intValue()==3){
            uri = ConstantUtil.CITY.replace("CITY",param);
        }
        else if(type.intValue()==4){
            uri = ConstantUtil.COUNTY.replace("COUNTY",param);
        }
         String s = restTemplate.getForObject(uri,String.class);
        JSONObject jsonObject = new JSONObject(s);

         return jsonObject;
    }

    /**
     * 地址校验,并返回对应的code
     * @param province
     * @param city
     * @param county
     */
      public String checkLocation(String province, String city, String county ){

          initMap();
          StringBuffer sb = new StringBuffer();
          List<String> keyList = provinceMap.entrySet()
                  .stream()
                  .filter(e-> province.equals(e.getValue()))
                  .map(Map.Entry::getKey).collect(Collectors.toList());
          if(keyList.size()>0){
              sb.append(keyList.stream().findFirst().get());
          }
          else{
              return "0";
          }
          List<String> cityList = getChina(2,sb.toString()).entrySet()
                  .stream()
                  .filter(e-> city.equals(e.getValue()))
                  .map(Map.Entry::getKey).collect(Collectors.toList());

          if(cityList.size()>0){
              sb.append(cityList.stream().findFirst().get());
          }
          else{
              return "0";
          }
          List<String> countyList = getChina(3, sb.toString()).entrySet()
                  .stream()
                  .filter(e-> county.equals(e.getValue()))
                  .map(Map.Entry::getKey).collect(Collectors.toList());;
          if(countyList.size()>0){
              sb.append(countyList.stream().findFirst().get());
          }
          else{
              return "0";
          }
          return  sb.toString();
      }

    /**
     * 获取稳定
     * @param locationCode
     * @return
     */
      private Integer getTemp(String locationCode){
          Map<String,Object> tempInfo = getChina(4, locationCode);

          Object s = tempInfo.get("weatherinfo");

          JSONObject jsonObject = new JSONObject(s);
          return jsonObject.getFloat("temp").intValue();
      }
    /**
     *
     * @param province
     * @param city
     * @param county
     * @return
     */
    @Retryable
    public Optional<Integer> getTemperature(String province, String city, String county){
            //数据不完整验证
           if(!StringUtils.hasText(province) || !StringUtils.hasText(city) || !StringUtils.hasText(county)){
               return Optional.of(ConstantUtil.RES_CODE_1);
           }
           String provinceNew = province.replace("省","").trim();
           String cityNew = city.replace("市","").trim();
           String countyNew = county.replace("区","").trim();
           //区域数据不存在
           String locationCode = checkLocation(provinceNew,cityNew,countyNew);
           //正常处理
           if(locationCode.equals("0")){
               return Optional.of(ConstantUtil.RES_CODE_2);
           }
         return Optional.of(getTemp(locationCode));
     }

}
