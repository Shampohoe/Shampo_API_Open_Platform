package com.shampo.project.service.impl.inner;

import com.shampo.project.service.UserInterfaceInfoService;
import com.shampo.shampocommon.service.InnerUserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * ClassName:InnerUserInterfaceInfoServiceImpl
 * Package:com.shampo.project.service.impl
 * Description:调用次数加1
 *
 * @Author kkli
 * @Create 2023/11/3 19:04
 * #Version 1.1
 */
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {
    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {

        return userInterfaceInfoService.invokeCount(interfaceInfoId,userId);
    }
}