package com.shsxt.crm.service;

import com.shsxt.Utils.AssertUtil;
import com.shsxt.Utils.Md5Util;
import com.shsxt.Utils.UserIDBase64;
import com.shsxt.base.BaseService;
import com.shsxt.crm.dao.UserMapper;
import com.shsxt.crm.model.UserModel;
import com.shsxt.crm.vo.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService  extends BaseService<User,Integer> {

    @Autowired
    private UserMapper userMapper;


    /**
     * 系统登录功能
     * @param userName
     * @param userPwd
     * @return
     */
    public UserModel login(String userName,String userPwd){
        /**
         * 1.参数校验
         *      用户名 非空
         *      密码  非空
         * 2.根据用户名  查询用户记录
         * 3.校验用户存在性
         *      不存在   --》记录不存在  方法结束
         * 4.用户存在
         *      校验密码
         *         密码错误---》密码不正确  方法结束
         * 5.密码正确
         *      用户登录成功  返回用户祥光信息
         */
        checkLoginParams(userName,userPwd);
        User user = userMapper.queryUserByUserName(userName);
        AssertUtil.isTrue(null==user,"用户已注销或用户不存在！");
        AssertUtil.isTrue(!(user.getUserPwd().equals(Md5Util.encode(userPwd))),"密码错误！");
        return buildUserModelInfo(user);
    }

    private UserModel buildUserModelInfo(User user) {
        return new UserModel(UserIDBase64.encoderUserID(user.getId()),user.getUserName(),user.getTrueName());
    }
    private void checkLoginParams(String userName, String userPwd) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(userPwd),"用户密码不能为空！");
    }


    /**
     * 密码修改
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserPassword(Integer userId,String oldPassword,String newPassword,String confirmPassword){
        /**
         * 1.参数校验
         *      userId  非空  记录必须存在
         *      oldPassword 非空  必须与数据库一致
         *      newPassword  非空  新密码不能与原始密码一致
         *      confirmPassword  非空  必须与新密码一致
         * 2.设置新密码
         *      新密码加密
         * 3.执行更新
         */
        User user = userMapper.selectByPrimaryKey(userId);
        checkParams(user,userId,oldPassword,newPassword,confirmPassword);
        user.setUserPwd(Md5Util.encode(newPassword));
        AssertUtil.isTrue(updateByPrimaryKeySelective(user)<1,"，密码更新失败");
    }

    private void checkParams(User user,Integer userId, String oldPassword, String newPassword, String confirmPassword) {
        AssertUtil.isTrue(null==userId || null==user,"用户未登录或不存在");
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword),"请输入原始密码");
        AssertUtil.isTrue(StringUtils.isBlank(newPassword),"请输入新密码");
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword),"请输入确认密码");
        AssertUtil.isTrue(!(newPassword.equals(confirmPassword)),"新密码输入不一致");
        AssertUtil.isTrue(oldPassword.equals(newPassword),"新密码不能与旧密码相同");
    }


}
