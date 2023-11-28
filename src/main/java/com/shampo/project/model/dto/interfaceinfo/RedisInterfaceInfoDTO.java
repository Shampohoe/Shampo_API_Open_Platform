package com.shampo.project.model.dto.interfaceinfo;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.beans.ConstructorProperties;
import java.io.Serializable;

/**
 * ClassName:RedisInterfaceInfoDTO
 * Package:com.shampo.project.model.dto.interfaceinfo
 * Description:
 *
 * @Author kkli
 * @Create 2023/11/27 23:01
 * #Version 1.1
 */
@Data
@NoArgsConstructor
public class RedisInterfaceInfoDTO implements Serializable {

    private String url;
    private String method;
    public RedisInterfaceInfoDTO(String url,String method){
        this.url=url;
        this.method=method;
    }
}
