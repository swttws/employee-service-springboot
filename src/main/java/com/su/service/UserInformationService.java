package com.su.service;

import com.su.domain.pojo.UserInformation;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2024-03-12
 */
public interface UserInformationService extends IService<UserInformation> {

    /**
     * 完善个人信息
     * @param userInformation
     * @return
     */
    Boolean updateInfo(UserInformation userInformation);
}
