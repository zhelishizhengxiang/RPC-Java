package com.simon.client.proxy;

import com.simon.client.circuitBreaker.CircuitBreakProvider;
import com.simon.client.circuitBreaker.CircuitBreaker;
import com.simon.client.retry.GuavaRetry;
import com.simon.client.rpcClient.RpcClient;
import com.simon.client.rpcClient.impl.NettyRpcClient;
import com.simon.client.serviceCenter.ServiceCenter;
import com.simon.client.serviceCenter.ZKServiceCenter;
import com.simon.common.message.RpcRequest;
import com.simon.common.message.RpcResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

/**
 * @author zhengx
 * @version 1.0
 * @Description 客户端代理类，用于处理远程方法的调用发送请求和接收响应的工作
 */
@AllArgsConstructor
@Slf4j
public class ClientProxy implements InvocationHandler {
    //目标类
    private RpcClient  rpcClient;

    //服务查询中心
    private ServiceCenter serviceCenter;

    //熔断器提供器
    private CircuitBreakProvider circuitBreakProvider;

    public ClientProxy() throws InterruptedException {
        this.serviceCenter=new ZKServiceCenter();
        this.circuitBreakProvider=new CircuitBreakProvider();
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //增强功能：构建request
        RpcRequest request=RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes())
                .build();
        //获取熔断器
        CircuitBreaker circuitBreaker=circuitBreakProvider.getCircuitBreaker(request.getInterfaceName());
        //判断熔断器是否允许请求经过
        if(!circuitBreaker.allowRequest()){
            log.warn("熔断器开启，请求被拒绝: {}", request);
            //这里可以针对熔断做特殊处理，返回特殊值
            return null;
        }
        RpcResponse response;
        //先访问服务查询中心做服务发现获取服务地址
        InetSocketAddress serviceAddress = serviceCenter.serviceDiscovery(request.getInterfaceName());
        //延迟实例化rpcClient
        rpcClient=new NettyRpcClient(serviceAddress);
        //获取方法签名，判断是否该服务在白名单，即是否可以重试
        String methodSignature = getMethodSignature(request.getInterfaceName(), method);
        if (serviceCenter.checkRetry(serviceAddress, methodSignature)) {
            //调用retry框架进行重试操作
            try {
                log.info("尝试重试调用服务: {}", methodSignature);
                response = new GuavaRetry().sendServiceWithRetry(request, rpcClient);
            } catch (Exception e) {
                log.error("重试调用失败: {}", methodSignature, e);
                circuitBreaker.recordFailure();
                throw e;  // 将异常抛给调用者
            }
        } else {
            //只调用一次
            response = rpcClient.sendRequest(request);
        }
        //记录response的状态，上报给熔断器
        if (response != null) {
            if (response.getCode() == 200) {
                circuitBreaker.recordSuccess();
            } else if (response.getCode() == 500) {
                circuitBreaker.recordFailure();
            }
            log.info("收到响应: {} 状态码: {}", request.getInterfaceName(), response.getCode());
        }


        return response != null ? response.getData() : null;
    }


    //动态生成一个实现指定接口或实现类所实现接口的代理对象
    public <T> T getProxy(Class<T> clazz) {
        Class<?>[] interfaces;
        //检查传入的类是否为接口
        if (clazz.isInterface()) {
            //如果是接口，直接使用这个接口创建代理
            interfaces = new Class<?>[]{clazz};
        } else {
            //如果是实现类，获取它实现的所有接口
            interfaces = clazz.getInterfaces();

        }
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, this);
    }

    /**
     * 获取方法签名：接口名#方法名(参数类型1,参数类型2,...)
     * @param interfaceName 接口名
     * @param method 方法
     * @return 方法签名
     */
    private String getMethodSignature(String interfaceName, Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(interfaceName).append("#").append(method.getName()).append("(");
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            sb.append(parameterTypes[i].getName());
            if (i < parameterTypes.length - 1) {
                sb.append(",");
            } else{
                sb.append(")");
            }
        }
        return sb.toString();
    }

    //关闭创建的资源
    //注：如果在需要C-S保持长连接的场景下无需调用close方法
    public void close(){
        rpcClient.close();
        serviceCenter.close();
    }
}