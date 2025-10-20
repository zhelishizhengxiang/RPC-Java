package com.simon.client.circuitBreaker;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhengx
 * @version 1.0
 * @Description: 服务熔断器
 * @Date: 2025/10/17
 */
@Slf4j
public class CircuitBreaker {
    //熔断器初始状态为关闭状态
    private CircuitBreakerState state = CircuitBreakerState.CLOSED;
    //对失败请求的数量
    private AtomicInteger failureCount = new AtomicInteger(0);
    //对成功请求的数量
    private AtomicInteger successCount = new AtomicInteger(0);
    //记录半开状态下请求的数量
    private AtomicInteger requestCount = new AtomicInteger(0);
    //失败次数阈值
    private final int failureThreshold;
    //半开启->关闭状态的成功次数比例
    private final double halfOpenSuccessRate;
    //恢复时间
    private final long retryTimePeriod;
    //上一次请求失败时间
    private long lastFailureTime = 0;

    public CircuitBreaker(int failureThreshold, double halfOpenSuccessRate,long retryTimePeriod) {
        this.failureThreshold = failureThreshold;
        this.halfOpenSuccessRate = halfOpenSuccessRate;
        this.retryTimePeriod = retryTimePeriod;
    }
    /**
     * 查看当前熔断器是否允许请求通过
     * @return true 允许 false 不允许
     * */
    public synchronized boolean allowRequest() {
        long currentTime = System.currentTimeMillis();
        log.info("熔断前检查, 当前失败次数：{}", failureCount);
        switch (state) {
            case OPEN:
                if (currentTime - lastFailureTime > retryTimePeriod) {
                    state = CircuitBreakerState.HALF_OPEN;
                    resetCounts();
                    log.info("熔断已解除，进入半开启状态，允许请求通过");
                    return true;
                }
                log.warn("熔断生效中，拒绝请求！");
                return false;
            case HALF_OPEN:
                log.info("当前为半开启状态，计数请求");
                requestCount.incrementAndGet();
                return true;
            case CLOSED:
            default:
                log.info("当前为正常状态，允许请求通过");
                return true;
        }
    }
    /**
     * 记录一个成功的请求：只有在半开状态才会记录成功请求次数
     * */
    public synchronized void recordSuccess() {
        if (state == CircuitBreakerState.HALF_OPEN) {
            successCount.incrementAndGet();
            if (successCount.get() >= halfOpenSuccessRate * requestCount.get()) {
                state = CircuitBreakerState.CLOSED;
                resetCounts();
                log.info("成功次数已达到阈值，熔断器切换至关闭状态");
            }
        } else {
            //如果不是半开状态，则不需要检查成功率，重置即可
            resetCounts();
            log.info("熔断器处于关闭状态，重置计数器");
        }
    }
    /**
     * 记录失败请求：关闭时超过阈值则打开熔断器；半开时只要失败就打开熔断器
     * */
    public synchronized void recordFailure() {
        failureCount.incrementAndGet();
        log.error("记录失败，当前失败次数：{}", failureCount);
        lastFailureTime = System.currentTimeMillis();
        //如果半开状态下
        if (state == CircuitBreakerState.HALF_OPEN) {
            state = CircuitBreakerState.OPEN;
            lastFailureTime = System.currentTimeMillis();
            log.warn("半开启状态下发生失败，熔断器切换至开启状态");
        } else if (failureCount.get() >= failureThreshold) {
            state = CircuitBreakerState.OPEN;
            log.error("失败次数已超过阈值，熔断器切换至开启状态");
        }
    }
    //重置次数
    private void resetCounts() {
        failureCount.set(0);
        successCount.set(0);
        requestCount.set(0);
    }

    public CircuitBreakerState getState() {
        return state;
    }
}

enum CircuitBreakerState {
    //关闭，开启，半开启
    CLOSED, OPEN, HALF_OPEN
}

