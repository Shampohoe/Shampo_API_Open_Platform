package com.shampo.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shampo.project.annotation.AuthCheck;
import com.shampo.project.common.BaseResponse;
import com.shampo.project.common.ErrorCode;
import com.shampo.project.common.ResultUtils;
import com.shampo.project.exception.BusinessException;
import com.shampo.project.mapper.UserInterfaceInfoMapper;
import com.shampo.project.model.vo.InterfaceInfoVO;
import com.shampo.project.service.InterfaceInfoService;
import com.shampo.shampocommon.model.entity.InterfaceInfo;
import com.shampo.shampocommon.model.entity.UserInterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分析控制器
 *
 * @author <a href="https://github.com/lishampo">程序员鱼皮</a>
 * @from <a href="https://shampo.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @GetMapping("/top/interface/invoke")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<InterfaceInfoVO>> listTopInvokeInterfaceInfo() {
        //查询调用次数最多的接口信息列表
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(3);
        //将接口信息按id分组，便于关联查询
        Map<Long, List<UserInterfaceInfo>> interfaceInfoIdObjMap = userInterfaceInfoList.stream()
                .collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));
        //创建查询接口信息的条件包装器
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        //设置查询条件，使用接口信息ID在接口信息映射中的键集合进行条件查询
        queryWrapper.in("id", interfaceInfoIdObjMap.keySet());
        //调用接口信息服务的list方法，传入条件包装器，获取符合条件的接口信息列表
        List<InterfaceInfo> list = interfaceInfoService.list(queryWrapper);
        //判断查询结果为空
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        //构建接口信息VO列表，使用流式处理将接口信息映射为接口信息VO对象，并加入列表
        List<InterfaceInfoVO> interfaceInfoVOList = list.stream().map(interfaceInfo -> {
            InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
            BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
            int totalNum = interfaceInfoIdObjMap.get(interfaceInfo.getId()).get(0).getTotalNum();
            interfaceInfoVO.setTotalNum(totalNum);

            return interfaceInfoVO;
        }).collect(Collectors.toList());
        //返回处理结果
        return ResultUtils.success(interfaceInfoVOList);
    }
}
