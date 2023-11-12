package com.shampo.shampo_interface.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName:RandomWordController
 * Package:com.shampo.shampo_interface.controller
 * Description:
 *
 * @Author kkli
 * @Create 2023/11/9 23:50
 * #Version 1.1
 */
@RestController
@RequestMapping("/random")
@Slf4j
public class RandomWordController {

    @GetMapping("chicken")
    public String getRandomChicken(){
        HttpResponse response = HttpRequest.get("https://api.btstu.cn/yan/api.php")
                .execute();
        return response.body();
    }

}
