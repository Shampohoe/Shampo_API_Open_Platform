package com.shampo.project.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.shampo.project.common.ErrorCode;
import com.shampo.project.constant.UserConstant;
import com.shampo.project.exception.BusinessException;
import com.shampo.project.mapper.UserInterfaceInfoMapper;
import com.shampo.project.model.dto.userinterfaceinfo.RedisUserInterfaceDTO;
import com.shampo.project.model.dto.userinterfaceinfo.UpdateUserInterfaceInfoDTO;
import com.shampo.project.model.vo.UserInterfaceInfoVO;
import com.shampo.project.service.InterfaceInfoService;
import com.shampo.project.service.UserInterfaceInfoService;
import com.shampo.project.service.UserService;
import com.shampo.shampocommon.model.entity.InterfaceInfo;
import com.shampo.shampocommon.model.entity.User;
import com.shampo.shampocommon.model.entity.UserInterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author 10488
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
* @createDate 2023-10-28 22:13:07
*/
@Service
@Slf4j
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {

    @Resource
    private RedissonClient redissonClient;

    @Autowired
    private RedisTemplate redisTemplate;
    @Resource
    private UserService userService;
    @Resource
    private InterfaceInfoService interfaceInfoService;

    private final static Gson gson=new Gson();
    private int numss=0;

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 创建时，所有参数必须非空
        if (add) {
            if (userInterfaceInfo.getInterfaceInfoId()<=0||userInterfaceInfo.getUserId()<=0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口或用户不存在");
            }
        }
        if (userInterfaceInfo.getLeftNum()<0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "调用次数用完");
        }
    }

    @Override
    public boolean invokeCount(long interfaceInfoId,long userId){
        if(interfaceInfoId<=0||userId<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        RedisUserInterfaceDTO redisUserInterfaceDTO=new RedisUserInterfaceDTO(userId,interfaceInfoId);
        // 将对象转换为 JSON 字符串
        String jsonString = gson.toJson(redisUserInterfaceDTO);
        //配置锁
        RLock lock = redissonClient.getLock(jsonString);
        //该线程唯一标识UUID，用于实现幂等性
        /*String uniqueIdentifier =Thread.currentThread().getName();
        log.info("+++++"+uniqueIdentifier);*/
        try {
            if(lock.tryLock(30, 15, TimeUnit.SECONDS)){
                    log.info(Thread.currentThread().getName()+"获取锁");
                    //后期要加锁或分布式锁进行优化
                    UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.eq("interfaceInfoId", interfaceInfoId);
                    updateWrapper.eq("userId", userId);
                    updateWrapper.setSql("leftNum=leftNum-1,totalNum=totalNum+1");
                    this.update(updateWrapper);
                return true;
            }else{
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        } finally {
            log.info(Thread.currentThread().getName()+"释放锁");
                lock.unlock();
        }
    }

    //展示用户所调用的接口
    @Override
    public List<UserInterfaceInfoVO> getInterfaceInfoByUserId(Long userId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        // 判断用户是否有权限
        if(!loginUser.getId().equals(userId) && !loginUser.getUserRole().equals(UserConstant.ADMIN_ROLE)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 获取用户可调用接口列表
        QueryWrapper<UserInterfaceInfo> userInterfaceInfoQueryWrapper= new QueryWrapper<>();
        userInterfaceInfoQueryWrapper.eq("userId",loginUser.getId());
        List<UserInterfaceInfo> userInterfaceInfoList = this.list(userInterfaceInfoQueryWrapper);

        Map<Long, List<UserInterfaceInfo>> interfaceIdUserInterfaceInfoMap = userInterfaceInfoList
                .stream().collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));

        Set<Long> interfaceIds = interfaceIdUserInterfaceInfoMap.keySet();
        QueryWrapper<InterfaceInfo> interfaceInfoQueryWrapper = new QueryWrapper<>();
        if(CollectionUtil.isEmpty(interfaceIds)){
            return new ArrayList<>();
        }

        interfaceInfoQueryWrapper.in("id",interfaceIds);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(interfaceInfoQueryWrapper);
        List<UserInterfaceInfoVO> userInterfaceInfoVOList = interfaceInfoList.stream().map(interfaceInfo -> {
            UserInterfaceInfoVO userInterfaceInfoVO = new UserInterfaceInfoVO();
            // 复制接口信息
            BeanUtils.copyProperties(interfaceInfo, userInterfaceInfoVO);
            userInterfaceInfoVO.setInterfaceStatus(Integer.valueOf(interfaceInfo.getStatus()));

            // 复制用户调用接口信息
            List<UserInterfaceInfo> userInterfaceInfos = interfaceIdUserInterfaceInfoMap.get(interfaceInfo.getId());
            UserInterfaceInfo userInterfaceInfo = userInterfaceInfos.get(0);
            BeanUtils.copyProperties(userInterfaceInfo, userInterfaceInfoVO);
            return userInterfaceInfoVO;
        }).collect(Collectors.toList());
        return userInterfaceInfoVOList;
    }

    @Override
    @Transactional
    public boolean updateUserInterfaceInfo(UpdateUserInterfaceInfoDTO updateUserInterfaceInfoDTO) {
        Long userId = updateUserInterfaceInfoDTO.getUserId();
        Long interfaceId = updateUserInterfaceInfoDTO.getInterfaceId();
        Long lockNum = updateUserInterfaceInfoDTO.getLockNum();

        if(interfaceId == null || userId == null || lockNum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        UserInterfaceInfo one = this.getOne(
                new QueryWrapper<UserInterfaceInfo>()
                        .eq("userId", userId)
                        .eq("interfaceInfoId", interfaceId)
        );
        RedisUserInterfaceDTO redisUserInterfaceDTO=new RedisUserInterfaceDTO(userId,interfaceId);
        if (one != null) {
            try{
                // 说明是增加数量
                this.update(
                        new UpdateWrapper<UserInterfaceInfo>()
                                .eq("userId", userId)
                                .eq("interfaceInfoId", interfaceId)
                                .setSql("leftNum = leftNum + " + lockNum)
                );
                one.setLeftNum((int)(one.getLeftNum()+lockNum));
                redisTemplate.opsForSet().add(redisUserInterfaceDTO,one);
                //设置过期时间:1天
                redisTemplate.expire(redisUserInterfaceDTO, 86400, TimeUnit.SECONDS);
                return true;
            }catch (Exception e){
                //错误则回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"保存错误");
            }
        } else {
            try{
                // 说明是第一次购买
                UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
                userInterfaceInfo.setUserId(userId);
                userInterfaceInfo.setInterfaceInfoId(interfaceId);
                userInterfaceInfo.setLeftNum(Math.toIntExact(lockNum));

                //先更新数据库再更新缓存
                this.save(userInterfaceInfo);
                redisTemplate.opsForSet().add(redisUserInterfaceDTO,userInterfaceInfo);
                //设置过期时间:1天
                redisTemplate.expire(redisUserInterfaceDTO, 86400, TimeUnit.SECONDS);
                return true;
            }catch (Exception e){
                //错误则回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"保存错误");
            }

        }

    }



}




