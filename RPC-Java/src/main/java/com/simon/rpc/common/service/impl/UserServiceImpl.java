package com.simon.rpc.common.service.impl;

import com.simon.rpc.common.pojo.User;
import com.simon.rpc.common.service.UserService;

import java.util.Random;
import java.util.UUID;

/**
 * @author zhengx
 * @version 1.0
 * @create 2025/10/7
 *  用户服务实现类
 */
public class UserServiceImpl implements UserService {

    @Override
    public User getUserById(Integer id) {
        System.out.println("客户端查询了" + id + "的用户");
        //模拟从数据库查询用户的行为
        Random random = new Random();
        User user = User.builder()
                .userName(UUID.randomUUID().toString())
                .id(id)
                .sex(random.nextBoolean())
                .build();
        return user;
    }


    @Override
    public Integer insertUser(User user) {
        System.out.println("插入数据成功" + user.getUserName());
        return user.getId();
    }
}