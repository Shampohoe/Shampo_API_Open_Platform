package com.shampo.project.model.vo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shampo.project.model.entity.InterfaceInfo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 帖子视图
 *
 */
@Data
public class InterfaceInfoVO implements Serializable {

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


}
