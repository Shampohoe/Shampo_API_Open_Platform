package com.shampo.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shampo.project.common.ErrorCode;
import com.shampo.project.exception.BusinessException;
import com.shampo.project.mapper.UserMapper;
import com.shampo.shampocommon.model.entity.User;
import com.shampo.shampocommon.service.InnerUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:InnerUserServiceImpl
 * Package:com.shampo.project.service.impl
 * Description:
 *
 * @Author kkli
 * @Create 2023/11/3 19:03
 * #Version 1.1
 */
@DubboService
public class InnerUserServiceImpl implements InnerUserService {
    @Resource
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public User getInvokeUser(String accessKey) {
        if(StringUtils.isAnyBlank(accessKey)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //先查缓存
        User redisuser = (User) redisTemplate.opsForValue().get(accessKey);
        if(redisuser!=null){
            return redisuser;
        }
        QueryWrapper<User>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("accessKey",accessKey);
        User user = userMapper.selectOne(queryWrapper);
        redisTemplate.opsForValue().set(accessKey,user,86400, TimeUnit.SECONDS);
        return user;
    }
}
