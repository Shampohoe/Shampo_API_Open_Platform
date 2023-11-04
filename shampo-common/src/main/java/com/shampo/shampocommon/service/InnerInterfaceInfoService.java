package com.shampo.shampocommon.service;

import com.shampo.shampocommon.model.entity.InterfaceInfo;


/**
* @author 10488
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-10-25 03:05:47
*/
public interface InnerInterfaceInfoService  {

     /**
      * 从数据库中查询模拟接口是否存在（请求路径、请求方法、请求参数）
      */
     InterfaceInfo getInterfaceInfo(String path, String method);

}
