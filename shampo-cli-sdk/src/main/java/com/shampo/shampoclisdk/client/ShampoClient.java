package com.shampo.shampoclisdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.shampo.shampoclisdk.model.User;
import com.shampo.shampoclisdk.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:ShampoClient
 * Package:com.shampo.shampo_interface.client
 * Description:
 *
 * @Author kkli
 * @Create 2023/10/26 15:21
 * #Version 1.1
 */
@Slf4j
public class ShampoClient extends CommonApiClient{

    public ShampoClient(String accessKey, String secretKey) {
        super(accessKey,secretKey);
    }


   /* public String getUsernameByPost()  {
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST+"/api/name/user")
                .addHeaders(getHeadMap("",accessKey,secretKey))
                .execute();
        log.info(String.valueOf(httpResponse.getStatus()));
        log.info("----------------");
        String result=httpResponse.body();
        return result;
    }*/
    public String getUsernameByPost(User user)  {
        HttpResponse httpResponse;
        if(user==null){
            log.info("null hahaha");
            User newuser=new User();
            String json = JSONUtil.toJsonStr(newuser);
            httpResponse = HttpRequest.post(GATEWAY_HOST+"/api/name/user")
                    .addHeaders(getHeadMap(json,accessKey,secretKey))
                    .body(json)
                    .execute();
        }else{
            String json = JSONUtil.toJsonStr(user);
            httpResponse = HttpRequest.post(GATEWAY_HOST+"/api/name/user")
                    .addHeaders(getHeadMap(json,accessKey,secretKey))
                    .body(json)
                    .execute();
        }

        log.info(String.valueOf(httpResponse.getStatus()));
        log.info("----------------");
        String result=httpResponse.body();
        return result;
    }

}
