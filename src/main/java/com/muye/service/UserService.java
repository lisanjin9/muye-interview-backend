package com.muye.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.muye.model.dto.user.UserQueryRequest;
import com.muye.model.dto.user.UserRegisterRequest;
import com.muye.model.dto.user.UserUpdateRequest;
import com.muye.model.entity.User;
import com.muye.model.vo.LoginUserVO;
import com.muye.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户评论服务
 */
public interface UserService extends IService<User> {

    /**
     * 校验数据
     *
     * @param user
     * @param add  对创建的数据进行校验
     */
    void validUser(User user, boolean add);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 获取用户评论封装
     *
     * @param user
     * @param request
     * @return
     */
    UserVO getUserVO(User user, HttpServletRequest request);

    /**
     * 分页获取用户评论封装
     *
     * @param userPage
     * @param request
     * @return
     */
    Page<UserVO> getUserVOPage(Page<User> userPage, HttpServletRequest request);

    /**
     * 用户注册
     * @param userRegisterRequest
     * @return
     */
    long registerUser(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     * @param userAccount 账号
     * @param userPassword 密码
     * @param request
     * @return
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录人
     * @param request
     * @return
     */
    LoginUserVO getLoginUser(HttpServletRequest request);

    /**
     * 用户推出
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 管理员根据id删除用户
     * @param id
     * @return
     */
    int deleteById(Long id);

}
