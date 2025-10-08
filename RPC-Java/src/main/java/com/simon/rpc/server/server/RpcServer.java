package com.simon.rpc.server.server;

/**
 * @author zhengx
 * @version 1.0
 * @create 2025/10/7
 *  RPC服务器接口
 */
public interface RpcServer {
    //开启监听
    void start(int port);
    void stop();
}