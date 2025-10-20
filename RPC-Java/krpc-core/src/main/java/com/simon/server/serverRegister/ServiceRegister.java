package com.simon.server.serverRegister;

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
    /**
     * 注册服务
     * @param clazz 服务类
     * @param serviceAddress 服务地址
     * */
    void register(Class<?> clazz, InetSocketAddress serviceAddress);
}
