package com.simon.server.serverRegister;

import com.simon.annotation.Retryable;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.server.serverRegister
 * @Description: 服务的注册中心
 * @Author: Simon
 * @CreateDate: 2025/10/9
 */
@Slf4j
public class ZKServiceRegister implements ServiceRegister {
    // curator 提供的zookeeper客户端
    private CuratorFramework client;
    //zookeeper根路径节点
    private static final String ROOT_PATH = "MyRPC";
    //重试白名单节点,只有幂等性服务才可以进行重试
     private static final String RETRY = "Retry";

    //负责zookeeper客户端的初始化
    public ZKServiceRegister(){
        // 充实策略：指数时间重试
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        // zookeeper的地址固定，不管是服务提供者还是，消费者都要与之建立连接
        // sessionTimeoutMs 与 zoo.cfg中的tickTime 有关系，
        // zk还会根据minSessionTimeout与maxSessionTimeout两个参数重新调整最后的超时值。默认分别为tickTime 的2倍和20倍
        // 使用心跳监听状态
        this.client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000)
                .retryPolicy(policy)
                .namespace(ROOT_PATH)
                .build();
        this.client.start();
        log.info("Zookeeper 连接成功");
    }
    /**
     * 注册服务到注册中心
     * @param clazz 服务类
     * @param serviceAddress 服务地址
     * */
    @Override
    public void register(Class<?> clazz , InetSocketAddress serviceAddress) {
        try {
            String serviceName=clazz.getName();
            // serviceName创建成永久节点，服务提供者下线时，不删服务名，只删地址
            if(client.checkExists().forPath("/" + serviceName) == null){
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/" + serviceName);
                log.info("服务节点 {} 创建成功", "/" + serviceName);
            }
            // 路径地址，一个/代表一个节点
            String path = "/" + serviceName +"/"+ getServiceAddress(serviceAddress);
            // 临时节点，服务端断开连接就自动删除节点
            if (client.checkExists().forPath(path) == null) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
                log.info("服务地址 {} 注册成功", path);
            } else {
                log.info("服务地址 {} 已经存在，跳过注册", path);
            }
            // 注册白名单
            List<String> retryableMethods = getRetryableMethod(clazz);
            log.info("可重试的方法: {}", retryableMethods);
            CuratorFramework rootClient = client.usingNamespace(RETRY);
            for (String retryableMethod : retryableMethods) {
                //白名单节点格式：/Retry/服务地址/方法签名（类名#方法名(参数类型1,参数类型2,...)）
                rootClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/" + getServiceAddress(serviceAddress) + "/" + retryableMethod);
            }
        } catch (Exception e) {
            System.out.println("此服务已存在");
        }
    }

    /**
     * 将服务地址转换为字符串
     * @param serverAddress 服务地址
     * @return 服务地址字符串
     * */
    private String getServiceAddress(InetSocketAddress serverAddress) {
        return serverAddress.getHostName() +
                ":" +
                serverAddress.getPort();
    }
    /**
     * 将服务地址字符串解析为InetSocketAddress对象
     * @param address 服务地址字符串
     * @return 服务地址对象
     * */
    private InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }

    /**
     * 获取该类可重试的方法
     * @param clazz 类对象
     * @return 可重试的方法列表
     * */
    private List<String> getRetryableMethod(Class<?> clazz){
        List<String> retryableMethods = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            //判断方法是否有可重试注解
            if (method.isAnnotationPresent(Retryable.class)) {
                String methodSignature = getMethodSignature(clazz, method);
                retryableMethods.add(methodSignature);
            }
        }
        return retryableMethods;
    }

     /**
     * 获取方法的签名：类名#方法名(参数类型1,参数类型2,...)
     * @param clazz 类对象
     * @param method 方法对象
     * @return 方法签名字符串
     * */
    private String getMethodSignature(Class<?> clazz, Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(clazz.getName()).append("#").append(method.getName()).append("(");
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
}
