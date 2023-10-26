package com.shampo.shampo_interface;

import com.shampo.shampo_interface.client.ShampoClient;
import com.shampo.shampo_interface.model.User;

/**
 * ClassName:Main
 * Package:com.shampo.shampo_interface
 * Description:
 *
 * @Author kkli
 * @Create 2023/10/26 15:52
 * #Version 1.1
 */
public class Main {
    public static void main(String[]args){
         String accessKey="shampo";
         String secretKey="abcdefg";
        ShampoClient shampoClient=new ShampoClient(accessKey,secretKey);
        String result1= shampoClient.getNameByGet("shampo");
        String result2=shampoClient.getNameByPost("shampohoe");
        User user=new User();
        user.setUsername("kkli");
        String result3=shampoClient.getUsernameByPost(user);
        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);
    }
}
