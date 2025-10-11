package com.simon.rpc.client.serviceCenter;

import com.simon.rpc.client.cache.ServiceCache;
import com.simon.rpc.client.serviceCenter.ZKWatcher.watchZK;
import com.simon.rpc.client.serviceCenter.balance.impl.ConsistencyHashBalance;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.client.serviceCenter
 * @Description: 服务的查询中心
 * @Author: Simon
 * @CreateDate: 2025/10/9
 */
public class ZKServiceCenter implements ServiceCenter{
    // curator 提供的zookeeper客户端
    private CuratorFramework client;
    //zookeeper根路径节点
    private static final String ROOT_PATH = "MyRPC";
    //客户端本地缓存
    private ServiceCache serviceCache=new ServiceCache();

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
        System.out.println("zookeeper 连接成功");
        //初始化缓存
        serviceCache=new ServiceCache();
        //使用监听器监听服务提供者的变化
        watchZK watchZK=new watchZK(client,serviceCache);
        //启动监听
        watchZK.watchChange(ROOT_PATH);
    }

    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            //先去本地缓存中招
            List<String> strings = serviceCache.getServiceFromCache(serviceName);
            //没有再去zookeeper中找
            if(strings==null){
                strings = client.getChildren().forPath("/" + serviceName);
            }
            // 负载均衡得到地址
            String address=new ConsistencyHashBalance().balance(strings);
            return parseAddress(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

}
