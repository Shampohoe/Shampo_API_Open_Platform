package com.shampo.shampoclisdk.client;

import cn.hutool.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * ClassName:ChickenSoup
 * Package:com.shampo.shampoclisdk.client
 * Description:随机返回毒鸡汤
 *
 * @Author kkli
 * @Create 2023/11/9 23:27
 * #Version 1.1
 */
@Slf4j
public class ChickenSoupClient extends CommonApiClient{
    public ChickenSoupClient(String accessKey, String secretKey) {
        super(accessKey,secretKey);
    }

    public String getRandomChicken(){
        return HttpRequest.get(GATEWAY_HOST+"/api/random/chicken")
                .addHeaders(getHeadMap("",accessKey,secretKey))
                .execute().body();
    }

}
