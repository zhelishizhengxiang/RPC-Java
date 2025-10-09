package com.simon.rpc.client;

import com.simon.rpc.client.proxy.ClientProxy;
import com.simon.rpc.common.pojo.User;
import com.simon.rpc.common.service.UserService;

/**
 * @author zhengx
 * @version 1.0
 * @create 2025/10/7
 *  RPC客户端测试类
 */
public class TestClient {
    public static void main(String[] args) {
        //客户端的代理对象
//        ClientProxy clientProxy=new ClientProxy("127.0.0.1",9999);
        //有了zookeeper注册中心，就可以动态获取服务地址了
        ClientProxy clientProxy=new ClientProxy();
        //获取要调用的对象的代理对象
        UserService proxy=clientProxy.getProxy(UserService.class);

        User user = proxy.getUserById(1);
        System.out.println("从服务端得到的user="+user.toString());

        User u= User.builder().id(100).userName("zhengx").sex(true).build();
        Integer id = proxy.insertUser(u);
        System.out.println("向服务端插入user的id"+id);
    }
}