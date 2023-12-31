package com.shampo.shampoclisdk.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * ClassName:SignUtils
 * Package:com.shampo.shampo_interface.utils
 * Description:
 *
 * @Author kkli
 * @Create 2023/10/26 21:09
 * #Version 1.1
 */
public class SignUtils {
    //签名算法
    public static String genSign(String body, String secretKey){
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
            String content;
        if(body!=null){
            content=body+"."+secretKey;
        }else{
            content="."+secretKey;
        }
        // 5393554e94bf0eb6436f240a4fd71282
        return md5.digestHex(content);
    }

}
