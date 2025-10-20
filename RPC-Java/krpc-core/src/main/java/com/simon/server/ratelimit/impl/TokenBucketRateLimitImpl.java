package com.simon.server.ratelimit.impl;

import com.simon.server.ratelimit.RateLimit;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.server.ratelimit.impl
 * @Description:令牌桶算法实现限流
 * @Author: Simon
 * @CreateDate: 2025/10/16
 */
public class TokenBucketRateLimitImpl implements RateLimit {
    //令牌产生速率（单位为ms）
    private   int rate;
    //桶容量
    private   int capacity;
    //当前桶容量
    private volatile int curCapcity;
    //记录上一次消费令牌的时间
    private volatile long lastTimestamp;
    public TokenBucketRateLimitImpl(int rate,int capacity){
        this.rate=rate;
        this.capacity=capacity;
        //直接先让桶内令牌数为n
        this.curCapcity=capacity;
        //初始化时间戳为当前时间
        this.lastTimestamp=System.currentTimeMillis();
    }
    @Override
    public  boolean getToken() {
        //只有桶更新的时候才会加锁
        synchronized (this){
            //如果当前桶还有剩余，就直接返回
            if(curCapcity>0){
                curCapcity--;
                return true;
            }
        }
        long currentTimestamp = System.currentTimeMillis();
        // 如果距离上一次请求的时间大于 RATE 的时间间隔
        if (currentTimestamp - lastTimestamp >= rate) {
            // 计算这段时间内生成的令牌数量
            int generatedTokens = (int) ((currentTimestamp - lastTimestamp) / rate);
            if (generatedTokens > 1) {
                // 只添加剩余令牌，确保不会超过桶的容量
                curCapcity = Math.min(capacity, curCapcity + generatedTokens - 1);
            }
            // 更新时间戳
            this.lastTimestamp = currentTimestamp;
            return true;
        }
        //获得不到，返回false
        return false;
    }
}
