package com.simon.server.ratelimit;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.server.ratelimit
 * @Description: 服务限流接口
 * @Author: Simon
 * @CreateDate: 2025/10/16
 */
public interface RateLimit {
    //获取访问许可
    boolean getToken();
}
