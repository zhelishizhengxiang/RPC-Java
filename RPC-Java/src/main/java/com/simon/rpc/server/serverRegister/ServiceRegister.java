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
    /**
     * 注册服务
     * @param serviceName 服务名
     * @param serviceAddress 服务地址
     * @param canRetry 是否可重试
     */
    void register(String serviceName, InetSocketAddress serviceAddress,boolean canRetry);
}
