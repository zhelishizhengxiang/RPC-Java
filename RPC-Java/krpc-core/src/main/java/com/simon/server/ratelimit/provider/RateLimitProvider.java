package com.simon.server.ratelimit.provider;

import com.simon.server.ratelimit.RateLimit;
import com.simon.server.ratelimit.impl.TokenBucketRateLimitImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.server.ratelimit.provider
 * @Description: 提供限流控制的一些具体服务，该限服务是以接口单位而不是已服务节点为单位
 * @Author: Simon
 * @CreateDate: 2025/10/16
 */
@Slf4j
public class RateLimitProvider {
    //存储每个接口对应的服务限流器实例
    Map<String, RateLimit> rateLimitMap =new ConcurrentHashMap<>();

    // 默认的限流桶容量和令牌生成速率
    private static final int DEFAULT_CAPACITY = 10;
    private static final int DEFAULT_RATE = 100;
    /**
     * 获取接口名称对应的服务限流器。如果不存在，则会创建一个新的实例并返回
     * @param interfaceName 接口名称
     * @return 对应接口的服务限流器
     * */
    // 提供限流实例
    public RateLimit getRateLimit(String interfaceName) {
        return rateLimitMap.computeIfAbsent(interfaceName, key -> {
            RateLimit rateLimit = new TokenBucketRateLimitImpl(DEFAULT_RATE, DEFAULT_CAPACITY);
            log.info("为接口 [{}] 创建了新的限流策略: {}", interfaceName, rateLimit);
            return rateLimit;
        });
    }
}