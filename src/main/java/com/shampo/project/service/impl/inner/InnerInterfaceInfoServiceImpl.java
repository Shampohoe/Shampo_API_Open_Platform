package com.shampo.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shampo.project.common.ErrorCode;
import com.shampo.project.exception.BusinessException;
import com.shampo.project.mapper.InterfaceInfoMapper;
import com.shampo.project.model.dto.interfaceinfo.RedisInterfaceInfoDTO;
import com.shampo.shampocommon.model.entity.InterfaceInfo;
import com.shampo.shampocommon.service.InnerInterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

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
    @Resource
    private RedisTemplate redisTemplate;
    private final static String INTERFACE="interface";
    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method) {
        if(StringUtils.isAnyBlank(url,method)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //先查缓存再查数据库
        RedisInterfaceInfoDTO redisInterfaceInfoDTO=new RedisInterfaceInfoDTO(url,method);
        InterfaceInfo interfaceInfo = (InterfaceInfo) redisTemplate.opsForValue().get(redisInterfaceInfoDTO);
        if(interfaceInfo!=null){
            return interfaceInfo;
        }
        //没命中缓存,则查数据库
        QueryWrapper<InterfaceInfo>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("url",url);
        queryWrapper.eq("method",method);
        InterfaceInfo interfaceInfo1= interfaceInfoMapper.selectOne(queryWrapper);
        if(interfaceInfo1==null){return null;}
        Long id = interfaceInfo1.getId();
        String interfaceId=INTERFACE+id;
        redisTemplate.opsForValue().set(interfaceId,redisInterfaceInfoDTO,10800, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(redisInterfaceInfoDTO,interfaceInfo1,10800, TimeUnit.SECONDS);
        return interfaceInfo1;
    }
}
