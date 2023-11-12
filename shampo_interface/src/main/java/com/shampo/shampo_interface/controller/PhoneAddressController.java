package com.shampo.shampo_interface.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.shampo.shampoclisdk.model.PhoneAddr;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName:PhoneAddressController
 * Package:com.shampo.shampo_interface.controller
 * Description:
 *
 * @Author kkli
 * @Create 2023/11/11 1:16
 * #Version 1.1
 */
@RestController
@RequestMapping("/phone")
@Slf4j
public class PhoneAddressController {
    @GetMapping("address")
    public String getPhoneAddress(@RequestBody PhoneAddr phone){
        HttpResponse response;
        if(phone.getPhone()==null){
             response = HttpRequest.get("https://qqlykm.cn/api/free/phone/get")
                    .execute();
        }else{
            response = HttpRequest.get("https://qqlykm.cn/api/free/phone/get"+"?phone="+phone)
                    .execute();
        }

        return response.body();
    }

}
