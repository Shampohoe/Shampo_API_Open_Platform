package com.shampo.shampoclisdk.client;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.shampo.shampoclisdk.model.Ip;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName:IpAddress
 * Package:com.shampo.shampoclisdk.client
 * Description:获取客户端ip地址
 *
 * @Author kkli
 * @Create 2023/11/10 1:35
 * #Version 1.1
 */
@Slf4j
public class IpAddressClient extends CommonApiClient{
    public IpAddressClient (String accessKey, String secretKey){super(accessKey,secretKey);}

   /* public String getIpAddress(){
        log.info("ip-client11");
        HttpResponse httpResponse = HttpRequest.get(GATEWAY_HOST + "/api/ip/address")
                .addHeaders(getHeadMap("", accessKey, secretKey))
                .execute();
        return httpResponse.body();
    }*/
    public String getIpAddress(Ip ip){
        HttpResponse httpResponse;
        if(ip==null){
            Ip newip=new Ip();
            String json = JSONUtil.toJsonStr(newip);
            httpResponse = HttpRequest.get(GATEWAY_HOST + "/api/ip/address")
                    .addHeaders(getHeadMap(json, accessKey, secretKey))
                    .body(json).execute();
        }else{
            String json = JSONUtil.toJsonStr(ip);
            httpResponse = HttpRequest.get(GATEWAY_HOST + "/api/ip/address")
                    .addHeaders(getHeadMap(json, accessKey, secretKey))
                    .body(json).execute();
        }
        return httpResponse.body();
    }

}
