package com.shampo.shampo_interface.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.shampo.shampoclisdk.model.Ip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName:IpAddressController
 * Package:com.shampo.shampo_interface.controller
 * Description: 返回要查询的ip地址，若参数值为空则返回客户端的ip地址
 *
 * @Author kkli
 * @Create 2023/11/10 1:39
 * #Version 1.1
 */
@RestController
@RequestMapping("/ip")
@Slf4j
public class IpAddressController {
    @GetMapping("address")
    public String getIpAddress(@RequestBody Ip ip){
        HttpResponse response;
        if(ip.getIp()==null){
             response = HttpRequest.get("https://qqlykm.cn/api/free/ip/get")
                    .execute();
        }else{
             response = HttpRequest.get("https://qqlykm.cn/api/free/ip/get"+"?ip="+ip.getIp())
                    .execute();
        }
        return response.body();
    }


}
