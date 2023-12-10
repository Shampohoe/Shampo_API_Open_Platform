package com.shampo.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.shampo.project.mapper.UserInterfaceInfoMapper;
import com.shampo.project.model.dto.userinterfaceinfo.RedisUserInterfaceDTO;
import com.shampo.project.service.UserInterfaceInfoService;
import com.shampo.shampocommon.model.entity.UserInterfaceInfo;
import com.shampo.shampocommon.service.InnerUserInterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:InnerUserInterfaceInfoServiceImpl
 * Package:com.shampo.project.service.impl
 * Description:调用次数加1
 *
 * @Author kkli
 * @Create 2023/11/3 19:04
 * #Version 1.1
 */
@DubboService(timeout = 10000,retries = 0)
@Slf4j
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {
    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceInfoId,userId);
    }
}
