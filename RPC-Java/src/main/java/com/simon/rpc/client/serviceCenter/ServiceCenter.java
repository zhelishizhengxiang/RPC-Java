package com.simon.rpc.client.serviceCenter;

import java.net.InetSocketAddress;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.client.serviceCenter
 * @Description: 服务查询中心接口
 * @Author: Simon
 * @CreateDate: 2025/10/9
 */
public interface ServiceCenter {
    /**
     * 根据服务名查找地址
     * @param serviceName 服务名
     * @return 服务地址,InetSocketAddress表示了一个网络地址,包含了IP地址和端口号
     */
    InetSocketAddress serviceDiscovery(String serviceName);
}
