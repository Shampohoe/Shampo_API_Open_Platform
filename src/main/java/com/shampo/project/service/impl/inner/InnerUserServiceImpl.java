package com.shampo.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shampo.project.common.ErrorCode;
import com.shampo.project.exception.BusinessException;
import com.shampo.project.mapper.UserMapper;
import com.shampo.shampocommon.model.entity.User;
import com.shampo.shampocommon.service.InnerUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

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

    @Override
    public User getInvokeUser(String accessKey) {
        if(StringUtils.isAnyBlank(accessKey)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("accessKey",accessKey);
        return userMapper.selectOne(queryWrapper);
    }
}
