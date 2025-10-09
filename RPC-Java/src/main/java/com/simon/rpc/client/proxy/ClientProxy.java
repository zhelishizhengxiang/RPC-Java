package com.simon.rpc.client.proxy;

import com.simon.rpc.client.rpcClient.RpcClient;
import com.simon.rpc.client.rpcClient.impl.NettyRpcClient;
import com.simon.rpc.common.message.RpcRequest;
import com.simon.rpc.common.message.RpcResponse;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author zhengx
 * @version 1.0
 * @purpose 处理远程方法的调用发送请求和接收响应的工作
 */
@AllArgsConstructor
public class ClientProxy implements InvocationHandler {
    //目标类
    private RpcClient  rpcClient;

    public ClientProxy() {
        this.rpcClient = new NettyRpcClient();
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
        //将信息发送给服务段让他来实现方法的调用并返回结果，接收响应
        RpcResponse response= rpcClient.sendRequest(request);
        //将数据库查询奥德User对象返回
        return response.getData();
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
            //如果实现类没有实现任何接口，则无法创建代理
            if (interfaces.length == 0) {
                throw new IllegalArgumentException("The class " + clazz.getName() + " does not implement any interface, cannot create proxy");
            }
        }
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, this);
    }
}