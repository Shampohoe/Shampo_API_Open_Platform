package com.shampo.shampo_interface.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.shampo.shampo_interface.model.User;
import com.shampo.shampo_interface.utils.SignUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
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
public class ShampoClient {

    private String accessKey;
    private String secretKey;

    public ShampoClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);

        String result= HttpUtil.get("http://localhost:8123/api/name/", paramMap);
        System.out.println(result);
        return result;
    }

    public String getNameByPost(@RequestParam String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);

        String result= HttpUtil.post("http://localhost:8123/api/name/", paramMap);
        System.out.println(result);
        return result;
    }

    //请求头
    private Map<String,String> getHeaderMap(String body){
        Map<String,String> hashmap=new HashMap<>();
        hashmap.put("accessKey",accessKey);
       // hashmap.put("secretKey",secretKey);
        hashmap.put("nonce", RandomUtil.randomNumbers(4));
        hashmap.put("body",body);
        hashmap.put("timestamp",String.valueOf(System.currentTimeMillis()/1000));
        hashmap.put("sign", SignUtils.genSign(body,secretKey));
        return  hashmap;

    }

    public String getUsernameByPost(@RequestBody User user){
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post("http://localhost:8123/api/name/user")
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        System.out.println(httpResponse.getStatus());
        String result=httpResponse.body();
        return result;
    }

}
