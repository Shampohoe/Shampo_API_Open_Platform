package com.shampo.shampocommon.service;

/**
* @author 10488
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2023-10-28 22:13:07
*/
public interface InnerUserInterfaceInfoService  {

    //调用接口统计
    boolean invokeCount(long interfaceInfoId,long userId);
}
