package com.simon.provider;


import com.simon.provider.impl.UserServiceImpl;
import com.simon.server.provider.ServiceProvider;
import com.simon.server.server.RpcServer;
import com.simon.server.server.impl.NettyRpcServer;
import com.simon.service.UserService;

/**
 * @author zhengx
 * @version 1.0
 * @create 2025/10/7
 *  RPC服务测试类
 */
public class ProviderTest {
    public static void main(String[] args) {
        UserService userService=new UserServiceImpl();
        //注册服务
        ServiceProvider serviceProvider=new ServiceProvider(9999, "127.0.0.1");
        serviceProvider.provideServiceInterface(userService,true);

        RpcServer rpcServer=new NettyRpcServer(serviceProvider);
        rpcServer.start(9999);
    }
}