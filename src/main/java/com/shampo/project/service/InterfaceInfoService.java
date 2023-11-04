package com.shampo.project.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.shampo.shampocommon.model.entity.InterfaceInfo;

/**
* @author 10488
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-10-25 03:05:47
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

     void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) ;


}
