package com.simon.rpc.client.retry;

import com.github.rholder.retry.*;
import com.simon.rpc.client.rpcClient.RpcClient;
import com.simon.rpc.common.message.RpcRequest;
import com.simon.rpc.common.message.RpcResponse;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.client.retry
 * @Description: 实现调用端请求的自定义的重试策略
 * @Author: Simon
 * @CreateDate: 2025/10/13
 */

public class GuavaRetry {
    private RpcClient rpcClient;

    /**
     * 发送带有重试机制的RPC请求，封装类发送请求的过程，使用guava retry框架实现重试
     * @param request 要发送的RPC请求对象
     * @param rpcClient 用于发送RPC请求的客户端对象
     * @return 返回RPC响应对象
     * */
    public RpcResponse sendServiceWithRetry(RpcRequest request, RpcClient rpcClient) {
        this.rpcClient=rpcClient;
        //使用retry构建器设置重试策略
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                //无论出现什么异常，都进行重试
                .retryIfException()
                //返回结果为 error时进行重试
                .retryIfResult(response -> Objects.equals(response.getCode(), 500))
                //重试等待策略：等待 2s 后再进行重试
                .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
                //重试停止策略：重试达到 3 次
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                //重试监听器，自定义触发重试时的处理工作。
                .withRetryListener(new RetryListener() {
                    @Override
                    /**
                     * 每次调用时会触发该方法，用于记录重试次数
                     * @param attempt 当前重试 Attempt 对象，包含重试次数、请求结果等信息
                     * 泛型V 表示重试操作返回的结果类型，这里是 RpcResponse
                     * */
                    public <V> void onRetry(Attempt<V> attempt) {
                        System.out.println("RetryListener: 第" + attempt.getAttemptNumber() + "次调用");
                    }
                })
                .build();
        try {
            //call()来执行实际的rpc请求，并且会根据重试策略继续重试
            return retryer.call(() -> rpcClient.sendRequest(request));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RpcResponse.fail();
    }
}
