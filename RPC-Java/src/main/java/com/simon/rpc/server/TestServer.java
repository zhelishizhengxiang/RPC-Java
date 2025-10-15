package com.simon.rpc.server;


import com.simon.rpc.server.provider.ServiceProvider;
import com.simon.rpc.server.server.RpcServer;
import com.simon.rpc.server.server.impl.NettyRpcServer;
import com.simon.rpc.common.service.UserService;
import com.simon.rpc.common.service.impl.UserServiceImpl;

/**
 * @author zhengx
 * @version 1.0
 * @create 2025/10/7
 *  RPC服务测试类
 */
public class TestServer {
    public static void main(String[] args) {
        UserService userService=new UserServiceImpl();
        //注册服务
        ServiceProvider serviceProvider=new ServiceProvider(9999, "127.0.0.1");
        serviceProvider.provideServiceInterface(userService,true);

        RpcServer rpcServer=new NettyRpcServer(serviceProvider);
        rpcServer.start(9999);
    }
}