package com.shampo.project.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shampo.project.common.ErrorCode;
import com.shampo.project.exception.BusinessException;
import com.shampo.project.mapper.InterfaceInfoMapper;

import com.shampo.project.service.InterfaceInfoService;
import com.shampo.shampocommon.model.entity.InterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 帖子服务实现
 *
 */
@Service
@Slf4j
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService {


    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();

        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(name)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            if (StringUtils.isAnyBlank(interfaceInfo.getUrl())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            if (StringUtils.isAnyBlank(interfaceInfo.getMethod())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
    }




}




