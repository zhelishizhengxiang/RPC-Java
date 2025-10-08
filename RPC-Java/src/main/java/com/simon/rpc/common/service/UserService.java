package com.simon.rpc.common.service;

import com.simon.rpc.common.pojo.User;

/**
 * @author zhengx
 * @version 1.0
 * @create 2025/10/7
 *  用户服务接口
 */
public interface UserService {

    //根据id获取用户
    User getUserById(Integer id);

    //新增用户
    Integer insertUser(User user);
}