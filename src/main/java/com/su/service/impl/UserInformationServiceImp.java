package com.su.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.su.common.exception.MyException;
import com.su.domain.pojo.UserInformation;
import com.su.mapper.UserInformationMapper;
import com.su.service.UserInformationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.su.utils.ThreadLocalUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2024-03-12
 */
@Service
public class UserInformationServiceImp extends ServiceImpl<UserInformationMapper, UserInformation> implements UserInformationService {

    /**
     * 完善个人信息
     *
     * @param userInformation
     * @return
     */
    @Override
    public Boolean updateInfo(UserInformation userInformation) {
        if (StringUtils.isEmpty(userInformation.getAddress())
                || StringUtils.isEmpty(userInformation.getEducation()) || StringUtils.isEmpty(userInformation.getMajor())
                || StringUtils.isEmpty(userInformation.getName()) || StringUtils.isEmpty(userInformation.getSchool())) {
            throw new MyException(500, "请填写完整个人信息");
        }
        Integer accountId = ThreadLocalUtil.getAccount().getId();
        UserInformation user = baseMapper.selectOne(Wrappers.<UserInformation>lambdaQuery()
                .eq(UserInformation::getAccountId, accountId));
        if (Objects.isNull(user)) {
            user = new UserInformation();
        }
        //设置用户信息
        user.setAccountId(accountId);
        user.setAddress(userInformation.getAddress());
        user.setCareer(userInformation.getCareer());
        user.setEducation(userInformation.getEducation());
        user.setMajor(userInformation.getMajor());
        user.setName(userInformation.getName());
        user.setSchool(userInformation.getSchool());
        user.setCreateTime(new Date());
        if (ObjectUtils.isEmpty(user.getId())) {
            baseMapper.insert(user);
        } else {
            baseMapper.updateById(user);
        }
        return true;
    }


}
