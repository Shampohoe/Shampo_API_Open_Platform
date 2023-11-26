package com.shampo.project.model.dto.userinterfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName:InvokeUserInterfaceInfoRequest
 * Package:com.shampo.project.model.dto.userinterfaceinfo
 * Description:
 *
 * @Author kkli
 * @Create 2023/11/24 11:36
 * #Version 1.1
 */
@Data
public class InvokeUserInterfaceInfoRequest implements Serializable {
    /**
     * 调用用户 id
     */
    private Long userId;

    /**
     * 接口 id
     */
    private Long interfaceInfoId;
}
