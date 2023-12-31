package com.shampo.project.model.dto.interfaceinfo;

import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

/**
 * 创建请求
 *
 */
@Data
public class InterfaceInfoAddRequest implements Serializable {


    /**
     * 用户名
     */
    private String name;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /*
     *请求参数
     */
    private String requestParams;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;


    /**
     * 请求类型
     */
    private String method;


    /**
     * 创建人
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}