package com.shampo.project.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shampo.project.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.shampo.project.model.entity.InterfaceInfo;
import com.shampo.project.model.vo.InterfaceInfoVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author 10488
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-10-25 03:05:47
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

     void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) ;


}
