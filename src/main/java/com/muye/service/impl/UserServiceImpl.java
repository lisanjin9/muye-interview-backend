package com.muye.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muye.common.ErrorCode;
import com.muye.constant.CommonConstant;
import com.muye.exception.BusinessException;
import com.muye.exception.ThrowUtils;
import com.muye.mapper.UserMapper;
import com.muye.model.dto.user.UserQueryRequest;
import com.muye.model.dto.user.UserRegisterRequest;
import com.muye.model.entity.Question;
import com.muye.model.entity.User;
import com.muye.model.vo.UserVO;
import com.muye.service.UserService;
import com.muye.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户评论服务实现
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 校验数据
     *
     * @param user
     * @param add  对创建的数据进行校验
     */
    @Override
    public void validUser(User user, boolean add) {
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
//        String title = user.getTitle();
        String title = null;
        // 创建数据时，参数不能为空
        if (add) {
            // todo 补充校验规则
            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则
        if (StringUtils.isNotBlank(title)) {
            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (userQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = userQueryRequest.getId();
        Long notId = userQueryRequest.getNotId();
        String title = userQueryRequest.getTitle();
        String content = userQueryRequest.getContent();
        String searchText = userQueryRequest.getSearchText();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        List<String> tagList = userQueryRequest.getTags();
        Long userId = userQueryRequest.getUserId();
        // todo 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取用户评论封装
     *
     * @param user
     * @param request
     * @return
     */
    @Override
    public UserVO getUserVO(User user, HttpServletRequest request) {
        // 对象转封装类
        UserVO userVO = null;
//        UserVO userVO = UserVO.objToVo(user);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
//        Long userId = user.getId();
//        User user = null;
//        if (userId != null && userId > 0) {
//            user = userService.getById(userId);
//        }
//        UserVO userVO = userService.getUserVO(user);
////        userVO.setUser(userVO);
//        // 2. 已登录，获取用户点赞、收藏状态
//        long userId = user.getId();
//        User loginUser = userService.getLoginUserPermitNull(request);
//        if (loginUser != null) {
//            // 获取点赞
//            QueryWrapper<UserThumb> userThumbQueryWrapper = new QueryWrapper<>();
//            userThumbQueryWrapper.in("userId", userId);
//            userThumbQueryWrapper.eq("userId", loginUser.getId());
//            UserThumb userThumb = userThumbMapper.selectOne(userThumbQueryWrapper);
//            userVO.setHasThumb(userThumb != null);
//            // 获取收藏
//            QueryWrapper<UserFavour> userFavourQueryWrapper = new QueryWrapper<>();
//            userFavourQueryWrapper.in("userId", userId);
//            userFavourQueryWrapper.eq("userId", loginUser.getId());
//            UserFavour userFavour = userFavourMapper.selectOne(userFavourQueryWrapper);
//            userVO.setHasFavour(userFavour != null);
//        }
        // endregion

        return userVO;
    }

    /**
     * 分页获取用户评论封装
     *
     * @param userPage
     * @param request
     * @return
     */
    @Override
    public Page<UserVO> getUserVOPage(Page<User> userPage, HttpServletRequest request) {
        List<User> userList = userPage.getRecords();
        Page<UserVO> userVOPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
//        if (CollUtil.isEmpty(userList)) {
//            return userVOPage;
//        }
//        // 对象列表 => 封装对象列表
//        List<UserVO> userVOList = userList.stream().map(user -> {
//            return UserVO.objToVo(user);
//        }).collect(Collectors.toList());
//
//        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
//        // region 可选
//        // 1. 关联查询用户信息
//        Set<Long> userIdSet = userList.stream().map(User::getUserId).collect(Collectors.toSet());
//        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
//                .collect(Collectors.groupingBy(User::getId));
//        // 2. 已登录，获取用户点赞、收藏状态
//        Map<Long, Boolean> userIdHasThumbMap = new HashMap<>();
//        Map<Long, Boolean> userIdHasFavourMap = new HashMap<>();
//        User loginUser = userService.getLoginUserPermitNull(request);
//        if (loginUser != null) {
//            Set<Long> userIdSet = userList.stream().map(User::getId).collect(Collectors.toSet());
//            loginUser = userService.getLoginUser(request);
//            // 获取点赞
//            QueryWrapper<UserThumb> userThumbQueryWrapper = new QueryWrapper<>();
//            userThumbQueryWrapper.in("userId", userIdSet);
//            userThumbQueryWrapper.eq("userId", loginUser.getId());
//            List<UserThumb> userUserThumbList = userThumbMapper.selectList(userThumbQueryWrapper);
//            userUserThumbList.forEach(userUserThumb -> userIdHasThumbMap.put(userUserThumb.getUserId(), true));
//            // 获取收藏
//            QueryWrapper<UserFavour> userFavourQueryWrapper = new QueryWrapper<>();
//            userFavourQueryWrapper.in("userId", userIdSet);
//            userFavourQueryWrapper.eq("userId", loginUser.getId());
//            List<UserFavour> userFavourList = userFavourMapper.selectList(userFavourQueryWrapper);
//            userFavourList.forEach(userFavour -> userIdHasFavourMap.put(userFavour.getUserId(), true));
//        }
//        // 填充信息
//        userVOList.forEach(userVO -> {
//            Long userId = userVO.getUserId();
//            User user = null;
//            if (userIdUserListMap.containsKey(userId)) {
//                user = userIdUserListMap.get(userId).get(0);
//            }
//            userVO.setUser(userService.getUserVO(user));
//            userVO.setHasThumb(userIdHasThumbMap.getOrDefault(userVO.getId(), false));
//            userVO.setHasFavour(userIdHasFavourMap.getOrDefault(userVO.getId(), false));
//        });
//        // endregion
//
//        userVOPage.setRecords(userVOList);
        return userVOPage;
    }

    @Override
    public long registerUser(UserRegisterRequest userRegisterRequest) {
        // 正则表达式依次为 至少包含一个：0-9数字 、 a-z 、 A-Z 、密码不包含空格 、 长度至少8位
        String regexPassword = "^[a-zA-Z0-9]{8,}";
        String regexAccount = "^[a-zA-Z0-9]{6,20}";
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        //判断密码和重复密码是否一致
        if (!checkPassword.equals(userPassword)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR , "两次密码不一致");
        }
        //校验密码是否合规
        boolean matchesPassword = userPassword.matches(regexPassword);
        if (!matchesPassword){
            throw new BusinessException(ErrorCode.OPERATION_ERROR , "密码不符合规定，密码要求: 至少包含0-9数字 、 a-z 、 A-Z ,密码不能包含空格，长度至少8位");
        }

        // 校验账号是否合规
        boolean matchesAccount = userAccount.matches(regexAccount);
        if (!matchesAccount) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR , "账号不符合规定");
        }

        //判断数据库中是否存在该账号
        queryWrapper.eq("userAccount" , userAccount);
        List<User> users = userMapper.selectList(queryWrapper);
        if (users.size()>0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"账号已存在");
        }

        //添加账号进入数据库
        User user = new User();
        user.setUserAccount(userRegisterRequest.getUserAccount());
        user.setUserPassword(userRegisterRequest.getUserPassword());
        int insert = userMapper.insert(user);
        return insert;
    }

}
