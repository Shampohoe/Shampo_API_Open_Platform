package com.shampo.shampo_interface.controller;


import com.shampo.shampoclisdk.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

/**
 * 名称 API
 *
 * @author yupi
 */
@RestController
@RequestMapping("/name")
@Slf4j
public class NameController {


    @GetMapping("/get")
    public String getNameByGet(String username,HttpServletRequest request) {
        System.out.println(request.getHeader("shampo"));
        System.out.println(request.getParameter("name"));
        System.out.println(request.getParameter("username"));
        return "GET 你的名字是" + username;
    }

    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name) {
        return "POST 你的名字是" + name;
    }

    @PostMapping("/user")
    public String getUsernameByPost(@RequestBody User user, HttpServletRequest request) throws Exception {
        log.info("*************************");

        String result= "POST 用户名是"+user.getUsername();


        return result;
    }

}
