package com.simon.client.circuitBreaker;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.client.circuitBreaker
 * @Description:
 * @Author: Simon
 * @CreateDate: 2025/10/17
 */
@Slf4j
public class CircuitBreakProvider {
    //<服务名，熔断器实例>
    private Map<String,CircuitBreaker> circuitBreakerMap=new ConcurrentHashMap<>();

    public synchronized CircuitBreaker getCircuitBreaker(String serviceName){
        // 使用 computeIfAbsent，避免手动同步
        return circuitBreakerMap.computeIfAbsent(serviceName, key -> {
            log.info("服务 [{}] 不存在熔断器，创建新的熔断器实例", serviceName);
            // 创建并返回新熔断器
            return new CircuitBreaker(1, 0.5, 10000);
        });
    }
}
