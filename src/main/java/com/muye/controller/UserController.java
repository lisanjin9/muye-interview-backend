package com.muye.controller;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.muye.annotation.AuthCheck;
import com.muye.common.BaseResponse;
import com.muye.common.DeleteRequest;
import com.muye.common.ErrorCode;
import com.muye.common.ResultUtils;
import com.muye.config.WxOpenConfig;
import com.muye.constant.UserConstant;
import com.muye.exception.BusinessException;
import com.muye.model.dto.user.UserAddRequest;
import com.muye.model.dto.user.UserLoginRequest;
import com.muye.model.dto.user.UserRegisterRequest;
import com.muye.model.dto.user.UserUpdateRequest;
import com.muye.model.entity.User;
import com.muye.model.vo.LoginUserVO;
import com.muye.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private WxOpenConfig wxOpenConfig;

    // region 登录相关

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        //判断用户传递的账号、密码、重复密码是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, (
                    StringUtils.isEmpty(userAccount) ? "账号不能为空" :
                            StringUtils.isEmpty(userPassword) ? "密码不能为空" : "重复密码不能为空")
            );
        }
        //进入业务层对账号密码进行校验
        long result = userService.registerUser(userRegisterRequest);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        request.getSession().setAttribute("loginUser", loginUserVO);
        return ResultUtils.success(loginUserVO);
    }

    /**
     *  获取当前登录用户
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        LoginUserVO loginUserVO = userService.getLoginUser(request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 用户退出
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (null == request) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result,result==true?"退出成功":"退出失败");
    }

    // endregion 登录相关

    // region 管理员管理用户代码

    /**
     * 管理员新增用户
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserRegisterRequest userRegisterRequest, HttpServletRequest request) {
        if (null == userRegisterRequest) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (null == request) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = 0;
        id = userService.registerUser(userRegisterRequest);
        return ResultUtils.success(id,id==0?"新增失败":"新增成功");
    }

    /**
     * 管理员删除用户
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if(null == deleteRequest || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.deleteById(deleteRequest.getId());
        return ResultUtils.success(result,result==1?"删除成功" : "删除失败");
    }

    // endregion




//    /**
//     * 用户登录（微信开放平台）
//     */
//    //@GetMapping("/login/wx_open")
//    public BaseResponse<LoginUserVO> userLoginByWxOpen(HttpServletRequest request, HttpServletResponse response,
//                                                       @RequestParam("code") String code) {
//        WxOAuth2AccessToken accessToken;
//        try {
//            WxMpService wxService = wxOpenConfig.getWxMpService();
//            accessToken = wxService.getOAuth2Service().getAccessToken(code);
//            WxOAuth2UserInfo userInfo = wxService.getOAuth2Service().getUserInfo(accessToken, code);
//            String unionId = userInfo.getUnionId();
//            String mpOpenId = userInfo.getOpenid();
//            if (StringUtils.isAnyBlank(unionId, mpOpenId)) {
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
//            }
////            return ResultUtils.success(userService.userLoginByMpOpen(userInfo, request));
//        } catch (Exception e) {
//            log.error("userLoginByWxOpen error", e);
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
//        }
//        return null; // 需要删除
//    }
//
//    /**
//     * 用户注销
//     *
//     * @param request
//     * @return
//     */
//    //@PostMapping("/logout")
//    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
//        if (request == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        boolean result = true;
////        boolean result = userService.userLogout(request);
//        return ResultUtils.success(result);
//    }
//
//    /**
//     * 获取当前登录用户
//     *
//     * @param request
//     * @return
//     */
//    //@GetMapping("/get/login")
//    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
////        User user = userService.getLoginUser(request);
////        return ResultUtils.success(userService.getLoginUserVO(user));
//        return null;
//    }
//
//    // endregion
//
//    // region 增删改查
//
//    /**
//     * 创建用户
//     *
//     * @param userAddRequest
//     * @param request
//     * @return
//     */
//    //@PostMapping("/add")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
//    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
//        if (userAddRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        User user = new User();
//        BeanUtils.copyProperties(userAddRequest, user);
//        // 默认密码 12345678
//        String defaultPassword = "12345678";
//        String encryptPassword = null;
////        String encryptPassword = DigestUtils.md5DigestAsHex((UserServiceImpl.SALT + defaultPassword).getBytes());
//        user.setUserPassword(encryptPassword);
//        boolean result = userService.save(user);
//        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
//        return ResultUtils.success(user.getId());
//    }
//
//    /**
//     * 删除用户
//     *
//     * @param deleteRequest
//     * @param request
//     * @return
//     */
//    //@PostMapping("/delete")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
//    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
//        if (deleteRequest == null || deleteRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        boolean b = userService.removeById(deleteRequest.getId());
//        return ResultUtils.success(b);
//    }
//
//    /**
//     * 更新用户
//     *
//     * @param userUpdateRequest
//     * @param request
//     * @return
//     */
//    //@PostMapping("/update")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
//    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
//                                            HttpServletRequest request) {
//        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        User user = new User();
//        BeanUtils.copyProperties(userUpdateRequest, user);
//        boolean result = userService.updateById(user);
//        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
//        return ResultUtils.success(true);
//    }
//
//    /**
//     * 根据 id 获取用户（仅管理员）
//     *
//     * @param id
//     * @param request
//     * @return
//     */
//    //@GetMapping("/get")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
//    public BaseResponse<User> getUserById(long id, HttpServletRequest request) {
//        if (id <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        User user = userService.getById(id);
//        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
//        return ResultUtils.success(user);
//    }
//
//    /**
//     * 根据 id 获取包装类
//     *
//     * @param id
//     * @param request
//     * @return
//     */
//    //@GetMapping("/get/vo")
//    public BaseResponse<UserVO> getUserVOById(long id, HttpServletRequest request) {
//        BaseResponse<User> response = getUserById(id, request);
//        User user = response.getData();
//        return ResultUtils.success(null);
////        return ResultUtils.success(userService.getUserVO(user));
//    }
//
//    /**
//     * 分页获取用户列表（仅管理员）
//     *
//     * @param userQueryRequest
//     * @param request
//     * @return
//     */
//    //@PostMapping("/list/page")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
//    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest,
//                                                   HttpServletRequest request) {
//        long current = userQueryRequest.getCurrent();
//        long size = userQueryRequest.getPageSize();
//        Page<User> userPage = userService.page(new Page<>(current, size),
//                userService.getQueryWrapper(userQueryRequest));
//        return ResultUtils.success(userPage);
//    }
//
//    /**
//     * 分页获取用户封装列表
//     *
//     * @param userQueryRequest
//     * @param request
//     * @return
//     */
//    //@PostMapping("/list/page/vo")
//    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,
//                                                       HttpServletRequest request) {
//        if (userQueryRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        long current = userQueryRequest.getCurrent();
//        long size = userQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        Page<User> userPage = userService.page(new Page<>(current, size),
//                userService.getQueryWrapper(userQueryRequest));
//        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
//        List<UserVO> userVO = null;
////        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
//        userVOPage.setRecords(userVO);
//        return ResultUtils.success(userVOPage);
//    }
//
//    // endregion
//
//    /**
//     * 更新个人信息
//     *
//     * @param userUpdateMyRequest
//     * @param request
//     * @return
//     */
//    //@PostMapping("/update/my")
//    public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest,
//                                              HttpServletRequest request) {
//        if (userUpdateMyRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        User loginUser = null;
////        User loginUser = userService.getLoginUser(request);
//        User user = new User();
//        BeanUtils.copyProperties(userUpdateMyRequest, user);
//        user.setId(loginUser.getId());
//        boolean result = userService.updateById(user);
//        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
//        return ResultUtils.success(true);
//    }
}
