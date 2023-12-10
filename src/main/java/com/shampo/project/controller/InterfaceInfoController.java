package com.shampo.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.shampo.project.annotation.AuthCheck;
import com.shampo.project.common.*;
import com.shampo.project.constant.CommonConstant;
import com.shampo.project.exception.BusinessException;
import com.shampo.project.model.dto.interfaceinfo.*;

import com.shampo.project.model.dto.userinterfaceinfo.RedisUserInterfaceDTO;
import com.shampo.project.model.enums.InterfaceInfoStatusEnum;
import com.shampo.project.service.InterfaceInfoService;
import com.shampo.project.service.UserInterfaceInfoService;
import com.shampo.project.service.UserService;
import com.shampo.shampoclisdk.client.ShampoClient;
import com.shampo.shampocommon.model.entity.InterfaceInfo;
import com.shampo.shampocommon.model.entity.User;
import com.shampo.shampocommon.model.entity.UserInterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 帖子接口
 *
 * @author shampo
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;
    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate redisTemplate;
    private final static String INTERFACE="interface";


    // region 增删改查

    /**
     * 创建
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newInterfaceInfoId = interfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        if(b){//删除缓存
            String interfaceId=INTERFACE+id;
            RedisInterfaceInfoDTO o = (RedisInterfaceInfoDTO) redisTemplate.opsForValue().get(interfaceId);
            if(o!=null){
                redisTemplate.opsForValue().getOperations().delete(interfaceId);
                redisTemplate.opsForValue().getOperations().delete(o);
            }
        }
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param interfaceInfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
                                            HttpServletRequest request) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        User user = userService.getLoginUser(request);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        if(result){//删除缓存
            String interfaceId=INTERFACE+id;
            RedisInterfaceInfoDTO o = (RedisInterfaceInfoDTO) redisTemplate.opsForValue().get(interfaceId);
            if(o!=null){
                redisTemplate.opsForValue().getOperations().delete(interfaceId);
                redisTemplate.opsForValue().getOperations().delete(o);
            }
        }
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String interfaceId=INTERFACE+id;
        //先查缓存，不命中再查数据库
        RedisInterfaceInfoDTO o = (RedisInterfaceInfoDTO) redisTemplate.opsForValue().get(interfaceId);
        InterfaceInfo interfaceInfo=null;
        if(o!=null){
            interfaceInfo = (InterfaceInfo) redisTemplate.opsForValue().get(o);
            if(interfaceInfo!=null){return ResultUtils.success(interfaceInfo);}
        }else if(interfaceInfo==null){
            interfaceInfo = interfaceInfoService.getById(id);
            String url = interfaceInfo.getUrl();
            String method = interfaceInfo.getMethod();
            RedisInterfaceInfoDTO redisInterfaceInfoDTO=new RedisInterfaceInfoDTO(url,method);
            redisTemplate.opsForValue().set(interfaceId,redisInterfaceInfoDTO,10800,TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(redisInterfaceInfoDTO,interfaceInfo,10800,TimeUnit.SECONDS);

        }

        return ResultUtils.success(interfaceInfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
        return ResultUtils.success(interfaceInfoList);
    }

    /**
     * 分页获取列表
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoQuery.getDescription();
        // description 需支持模糊搜索
        interfaceInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfaceInfoPage);
    }

    // endregion

    /**
     * 发布接口
     *
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/online")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest,
                                                     HttpServletRequest request)   {

        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 判断是否存在
        long id=idRequest.getId();
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }


        // 仅本人或管理员可修改
        InterfaceInfo interfaceInfo=new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
        boolean result1 = interfaceInfoService.updateById(interfaceInfo);
        //更新缓存
        if(result1){
            InterfaceInfo newInterfaceInfo = interfaceInfoService.getById(id);
            String interfaceId=INTERFACE+id;
            String url = newInterfaceInfo.getUrl();
            String method = newInterfaceInfo.getMethod();
            RedisInterfaceInfoDTO redisInterfaceInfoDTO=new RedisInterfaceInfoDTO(url,method);
            redisTemplate.opsForValue().set(interfaceId,redisInterfaceInfoDTO,10800,TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(redisInterfaceInfoDTO,newInterfaceInfo,10800,TimeUnit.SECONDS);
        }

        return ResultUtils.success(result1);
    }

    /**
     * 下线
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest,
                                                     HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 判断是否存在
        long id=idRequest.getId();
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 仅本人或管理员可修改
        InterfaceInfo interfaceInfo=new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());
        boolean result1 = interfaceInfoService.updateById(interfaceInfo);
        //删除缓存
        if(result1){
            String interfaceId=INTERFACE+id;
            RedisInterfaceInfoDTO o = (RedisInterfaceInfoDTO) redisTemplate.opsForValue().get(interfaceId);
            if(o!=null){
                redisTemplate.opsForValue().getOperations().delete(interfaceId);
                redisTemplate.opsForValue().getOperations().delete(o);
            }
        }

        return ResultUtils.success(result1);
    }


    /**
     * 测试调用
     * @param interfaceInfoInvokeRequest
     * @param request
     * @return
     */
    @PostMapping("/invoke")
    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
                                                      HttpServletRequest request)   {
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 1.判断是否存在
        long id=interfaceInfoInvokeRequest.getId();
        String interfaceId=INTERFACE+id;
        //先查缓存
        InterfaceInfo oldInterfaceInfo = null;
        RedisInterfaceInfoDTO o = (RedisInterfaceInfoDTO) redisTemplate.opsForValue().get(interfaceId);
        if(o!=null){
            oldInterfaceInfo = (InterfaceInfo) redisTemplate.opsForValue().get(o);
        }else if(oldInterfaceInfo==null){
            //再查数据库,并更新缓存
            oldInterfaceInfo = interfaceInfoService.getById(id);
            String url = oldInterfaceInfo.getUrl();
            String method = oldInterfaceInfo.getMethod();
            RedisInterfaceInfoDTO redisInterfaceInfoDTO=new RedisInterfaceInfoDTO(url,method);
            redisTemplate.opsForValue().set(redisInterfaceInfoDTO,oldInterfaceInfo,10800, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(interfaceId,redisInterfaceInfoDTO,10800, TimeUnit.SECONDS);

        }
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if(oldInterfaceInfo.getStatus()==InterfaceInfoStatusEnum.OFFLINE.getValue()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口已关闭");
        }

        User loginUser=userService.getLoginUser(request);
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();


        //2.用户调用次数校验
        // TODO 设置缓存，设置读写锁
        long userId = loginUser.getId();
        RedisUserInterfaceDTO redisUserInterfaceDTO=new RedisUserInterfaceDTO(userId,id);
        UserInterfaceInfo userInterfaceInfo = (UserInterfaceInfo) redisTemplate.opsForValue().get(redisUserInterfaceDTO);
        if(userInterfaceInfo==null){
            QueryWrapper<UserInterfaceInfo> userInterfaceInfoQueryWrapper = new QueryWrapper<>();
            userInterfaceInfoQueryWrapper.eq("userId", userId);
            userInterfaceInfoQueryWrapper.eq("interfaceInfoId", id);
            userInterfaceInfo = userInterfaceInfoService.getOne(userInterfaceInfoQueryWrapper);
            redisTemplate.opsForValue().set(redisUserInterfaceDTO,userInterfaceInfo);
            //设置过期时间:半天
            redisTemplate.expire(redisUserInterfaceDTO, 43200, TimeUnit.SECONDS);
        }
        if (userInterfaceInfo == null){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用次数不足！");
        }
        int leftNum = userInterfaceInfo.getLeftNum();
        if(leftNum <= 0){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用次数不足！");
        }

        //3.发起接口调用
        String requestParams= interfaceInfoInvokeRequest.getUserRequestParams();
        Object res = invokeInterfaceInfo(oldInterfaceInfo.getSdk(), oldInterfaceInfo.getName(), requestParams, accessKey, secretKey);
        if (res == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (res.toString().contains("Error request")) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用错误，请检查参数和接口调用次数！");
        }
        return ResultUtils.success(res);
    }


    private Object invokeInterfaceInfo(String classPath, String methodName, String userRequestParams,
                                       String accessKey, String secretKey) {
        try {

            Class<?> clientClazz = Class.forName(classPath);
            // 1. 获取构造器，参数为ak,sk
            Constructor<?> binApiClientConstructor = clientClazz.getConstructor(String.class, String.class);
            // 2. 构造出客户端
            Object apiClient =  binApiClientConstructor.newInstance(accessKey, secretKey);

            // 3. 找到要调用的方法
            Method[] methods = clientClazz.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    // 3.1 获取参数类型列表
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    log.info(parameterTypes.toString()+"******");
                    if (parameterTypes.length == 0) {
                        // 如果没有参数，直接调用
                        return method.invoke(apiClient);
                    }
                    Gson gson = new Gson();
                    // 构造参数
                    Object parameter= gson.fromJson(userRequestParams, parameterTypes[0]);
                    //log.info(parameter.getClass().toString());
                    return method.invoke(apiClient, parameter);
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "找不到调用的方法!! 请检查你的请求参数是否正确!");
        }
    }

}
