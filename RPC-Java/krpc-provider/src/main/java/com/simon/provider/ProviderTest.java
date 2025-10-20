package com.simon.provider;


import com.simon.KRpcApplication;
import com.simon.provider.impl.UserServiceImpl;
import com.simon.server.provider.ServiceProvider;
import com.simon.server.server.RpcServer;
import com.simon.server.server.impl.NettyRpcServer;
import com.simon.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProviderTest {
    public static void main(String[] args) {
        KRpcApplication.initialize();
        UserService userService=new UserServiceImpl();
        //注册服务
        ServiceProvider serviceProvider=new ServiceProvider(9999, "127.0.0.1");
        serviceProvider.provideServiceInterface(userService);

        RpcServer rpcServer=new NettyRpcServer(serviceProvider);
        rpcServer.start(9999);
        log.info("服务端启动成功,监听端口9999");
    }
}