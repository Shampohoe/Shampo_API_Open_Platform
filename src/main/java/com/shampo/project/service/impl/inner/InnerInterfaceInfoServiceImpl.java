package com.shampo.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shampo.project.common.ErrorCode;
import com.shampo.project.exception.BusinessException;
import com.shampo.project.mapper.InterfaceInfoMapper;
import com.shampo.shampocommon.model.entity.InterfaceInfo;
import com.shampo.shampocommon.service.InnerInterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * ClassName:InnerInterfaceInfoServiceImpl
 * Package:com.shampo.project.service.impl
 * Description:
 *
 * @Author kkli
 * @Create 2023/11/3 19:04
 * #Version 1.1
 */
@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {
    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;
    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method) {
        if(StringUtils.isAnyBlank(url,method)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("url",url);
        queryWrapper.eq("method",method);
        return interfaceInfoMapper.selectOne(queryWrapper);
    }
}
