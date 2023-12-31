package com.shampo.project.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.shampo.project.model.dto.userinterfaceinfo.UpdateUserInterfaceInfoDTO;
import com.shampo.project.model.vo.UserInterfaceInfoVO;
import com.shampo.shampocommon.model.entity.UserInterfaceInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 10488
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2023-10-28 22:13:07
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) ;

    boolean invokeCount(long interfaceInfoId,long userId);

    List<UserInterfaceInfoVO> getInterfaceInfoByUserId(Long userId, HttpServletRequest request);

    boolean updateUserInterfaceInfo(UpdateUserInterfaceInfoDTO updateUserInterfaceInfoDTO);
}
