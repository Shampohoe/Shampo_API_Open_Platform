package com.shampo.project.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName:InterfaceInfoInvokeRequest
 * Package:com.shampo.project.model.dto.interfaceinfo
 * Description:接口调用请求
 *
 * @Author kkli
 * @Create 2023/10/27 22:41
 * #Version 1.1
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /*
     *请求参数
     */
    private String userRequestParams;

    private static final long serialVersionUID = 1L;
}
