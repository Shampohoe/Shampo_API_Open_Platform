package com.shampo.shampo_interface.controller;


import com.shampo.shampoclisdk.model.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 名称 API
 *
 * @author yupi
 */
@RestController
@RequestMapping("/")
public class NameController {


    @GetMapping("/name")
    public String getNameByGet(String username,HttpServletRequest request) {
        System.out.println(request.getHeader("shampo"));
        System.out.println(request.getParameter("name"));
        System.out.println(request.getParameter("username"));
        return "GET 你的名字是" + username;
    }

    @PostMapping("/")
    public String getNameByPost(@RequestParam String name) {
        return "POST 你的名字是" + name;
    }

    @PostMapping("/user")
    public String getUsernameByPost(@RequestBody User user, HttpServletRequest request){
        String accessKey=request.getHeader("accessKey");

        String nonce=request.getHeader("nonce");
        String timeStamp=request.getHeader("timeStamp");
        String sign=request.getHeader("sign");
        String body=request.getHeader("body");

        //实际去数据库查有没有对应的accessKey
        /*if(!ifExist){
            throw new RuntimeException("无权限");
        }*/
        if(Long.parseLong(nonce)>10000){
            throw new RuntimeException("无权限");
        }
        //时间和当前时间不能超过5分钟
        /*if(timeStamp){
            throw new RuntimeException("无权限");
        }*/
        //实际情况是从数据库中查出secretKey

        /*String serverSign= SignUtils.genSign(body,secretKey);
        if(!sign.equals(serverSign)){
            throw new RuntimeException("无权限");
        }*/
        String result= "POST 用户名是"+user.getUsername();


        return result;
    }

}
