package com.shampo.shampoclisdk.client;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.shampo.shampoclisdk.model.PhoneAddr;
import lombok.extern.slf4j.Slf4j;


/**
 * ClassName:PhoneAddressClient
 * Package:com.shampo.shampoclisdk.client
 * Description:
 *
 * @Author kkli
 * @Create 2023/11/10 23:35
 * #Version 1.1
 */

@Slf4j
public class PhoneAddressClient extends CommonApiClient{
    public PhoneAddressClient(String accessKey, String secretKey){super(accessKey,secretKey);}

   /* public String getPhoneAddress(){
        HttpResponse httpResponse =HttpRequest.get(GATEWAY_HOST+"/api/phone/address")
                .addHeaders(getHeadMap("",accessKey,secretKey))
                .execute();
        return httpResponse.body();
    }*/
    public String getPhoneAddress(PhoneAddr phone){
        HttpResponse httpResponse;
        if(phone==null){
            PhoneAddr newphone=new PhoneAddr();
            String json = JSONUtil.toJsonStr(newphone);
            httpResponse =HttpRequest.get(GATEWAY_HOST+"/api/phone/address")
                    .addHeaders(getHeadMap(json,accessKey,secretKey))
                    .body(json).execute();
        }else{
            String json = JSONUtil.toJsonStr(phone);
            httpResponse =HttpRequest.get(GATEWAY_HOST+"/api/phone/address")
                    .addHeaders(getHeadMap(json,accessKey,secretKey))
                    .body(json).execute();
        }
        return httpResponse.body();
    }

}
