package com.shampo.project.model.dto.interfaceinfo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

/**
 * 更新请求
 *
 */
@Data
public class InterfaceInfoUpdateRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;


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

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 接口状态0关闭1开启
     */
    private Integer status;

    /**
     * 请求类型
     */
    private String method;


    private static final long serialVersionUID = 1L;
}