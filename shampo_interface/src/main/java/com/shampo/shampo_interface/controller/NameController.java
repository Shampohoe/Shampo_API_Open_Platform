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


    @PostMapping("/user")
    public String getUsernameByPost(@RequestBody User user, HttpServletRequest request) throws Exception {
        log.info("*************************");
        String result=null;
        if(user.getUsername()!=null){
            result= "POST 用户名是"+user.getUsername();
        }else{
            result="";
        }
        return result;
    }

}
