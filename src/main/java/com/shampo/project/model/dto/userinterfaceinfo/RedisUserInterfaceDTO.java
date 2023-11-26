package com.shampo.project.model.dto.userinterfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName:RedisUserInterfaceDTO
 * Package:com.shampo.project.model.dto.userinterfaceinfo
 * Description:
 *
 * @Author kkli
 * @Create 2023/11/19 15:37
 * #Version 1.1
 */
@Data
public class RedisUserInterfaceDTO implements Serializable {

    private long userId;
    private long interfaceId;
    public RedisUserInterfaceDTO(long userId,long interfaceId){
        this.userId=userId;
        this.interfaceId=interfaceId;
    }
}
