package com.simon.rpc.server.serverRegister;

import java.net.InetSocketAddress;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.server.serverRegister
 * @Description: 服务注册接口
 * @Author: Simon
 * @CreateDate: 2025/10/9
 */
public interface ServiceRegister {
    //注册：保存服务与地址
    void register(String serviceName, InetSocketAddress serviceAddress);
}
