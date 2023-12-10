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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

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
    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private final static Gson gson=new Gson();
    private final static String  EXCHANGE_NAME = "shampo.direct";
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
        try {
            if(lock.tryLock(30, 15, TimeUnit.SECONDS)){
                UserInterfaceInfo o = (UserInterfaceInfo)redisTemplate.opsForValue().get(redisUserInterfaceDTO);
                if(o==null){
                    QueryWrapper<UserInterfaceInfo> queryWrapper=new QueryWrapper<>();
                    queryWrapper.eq("interfaceInfoId",interfaceInfoId);
                    queryWrapper.eq("userId",userId);
                    UserInterfaceInfo result=userInterfaceInfoMapper.selectOne(queryWrapper);
                    int leftNum = result.getLeftNum();
                    if(leftNum<=0){
                        return false;//调用次数不足
                    }
                    result.setLeftNum(leftNum-1);
                    result.setTotalNum(result.getTotalNum()+1);
                    redisTemplate.opsForValue().set(redisUserInterfaceDTO,result);
                    //设置过期时间:半天
                    redisTemplate.expire(redisUserInterfaceDTO, 43200, TimeUnit.SECONDS);
                }else{
                    int leftNum = o.getLeftNum();
                    if(leftNum<=0){
                        return false;//调用次数不足
                    }
                    o.setTotalNum(o.getTotalNum()+1);
                    o.setLeftNum(leftNum-1);
                    redisTemplate.opsForValue().set(redisUserInterfaceDTO,o);
                }
                // 消息
                UserInterfaceInfo newo = (UserInterfaceInfo)redisTemplate.opsForValue().get(redisUserInterfaceDTO);
                // MQ消息发布
                rabbitTemplate.convertAndSend(EXCHANGE_NAME, "sql", newo);
                return true;
            }else{
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        } finally {
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
                redisTemplate.opsForValue().set(redisUserInterfaceDTO,one);
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
                redisTemplate.opsForValue().set(redisUserInterfaceDTO,userInterfaceInfo);
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




