package com.shampo.project.model.vo;


import lombok.Data;

import java.io.Serializable;

/**
 * 帖子视图
 *
 */
@Data
public class InterfaceInfoVO implements Serializable {

    private String name;
    /**
     * 调用次数
     */
    private Integer totalNum;
    /**
     * 计费规则（元/条）
     */
    private Double charging;

    /**
     * 计费Id
     */
    private Long chargingId;

    /**
     * 接口剩余可调用次数
     */
    private String availablePieces;

    private static final long serialVersionUID = 1L;


}
