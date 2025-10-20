package com.simon.client.serviceCenter;


import com.simon.client.cache.ServiceCache;
import com.simon.client.serviceCenter.ZKWatcher.watchZK;
import com.simon.client.serviceCenter.balance.impl.ConsistencyHashBalance;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.client.serviceCenter
 * @Description: 服务的查询中心
 * @Author: Simon
 * @CreateDate: 2025/10/9
 */
@Slf4j
public class ZKServiceCenter implements ServiceCenter{
    // curator 提供的zookeeper客户端
    private CuratorFramework client;
    //zookeeper根路径节点
    private static final String ROOT_PATH = "MyRPC";
    //重试白名单节点,只有幂等性服务才可以进行重试
    private static final String RETRY = "Retry";
    //客户端本地缓存
    private ServiceCache serviceCache=new ServiceCache();
    //负载均衡策略
    private  final ConsistencyHashBalance loadBalance=new ConsistencyHashBalance();
    //白名单缓存
    private Set<String> retryServiceCache = new CopyOnWriteArraySet<>();

    //负责zookeeper客户端的初始化，并与zookeeper服务端进行连接
    public ZKServiceCenter() throws InterruptedException {
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
        log.info("zookeeper 连接成功");
        //初始化缓存
        serviceCache=new ServiceCache();
        //使用监听器监听服务提供者的变化
        watchZK watchZK=new watchZK(client,serviceCache);
        //启动监听
        watchZK.watchChange(ROOT_PATH);
    }

    /**
     * 根据服务名查询服务地址：本地缓存——注册中心——负载均衡
     * @param serviceName 服务名
     * @return 服务地址
     * */
    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            //先去本地缓存中招
            List<String> addressList = serviceCache.getServiceFromCache(serviceName);
            //没有再去zookeeper中找
            //这种i情况基本不会发生，或者说只会出现在初始化阶段
            if(addressList==null){
                addressList = client.getChildren().forPath("/" + serviceName);
                // 如果本地缓存中没有该服务名的地址列表，则添加
                List<String> cachedAddresses = serviceCache.getServiceFromCache(serviceName);
                if (cachedAddresses == null || cachedAddresses.isEmpty()) {
                    // 假设 addServiceToCache 方法可以处理单个地址
                    for (String address : addressList) {
                        serviceCache.addServiceToCache(serviceName, address);
                    }
                }
            }
            if (addressList.isEmpty()) {
                log.warn("未找到服务：{}", serviceName);
                return null;
            }
            
            // 负载均衡得到地址
            String address=loadBalance.balance(addressList);
            return parseAddress(address);
        } catch (Exception e) {
            log.error("服务发现失败，服务名：{}", serviceName, e);
        }
        return null;
    }

    @Override
    public boolean checkRetry(InetSocketAddress serviceAddress, String methodSignature) {
        if (retryServiceCache.isEmpty()) {
            try {
                CuratorFramework rootClient = client.usingNamespace(RETRY);
                List<String> retryableMethods = rootClient.getChildren().forPath("/" + getServiceAddress(serviceAddress));
                retryServiceCache.addAll(retryableMethods);
            } catch (Exception e) {
                log.error("检查重试失败，方法签名：{}", methodSignature, e);
            }
        }
        return retryServiceCache.contains(methodSignature);
    }

    /**
    * @Param serverAddress: 服务端地址
    * @Return: java.lang.String
    * @Description: 地址 -> XXX.XXX.XXX.XXX:port 字符串
    * */
    private String getServiceAddress(InetSocketAddress serverAddress) {
        return serverAddress.getHostName() +
                ":" +
                serverAddress.getPort();
    }
    /**
     * @Param address: 服务端地址字符串
     * @Return: java.net.InetSocketAddress
     * @Description: 字符串解析为地址
     * */
    private InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }

    @Override
    public void close() {
        client.close();
    }

}
