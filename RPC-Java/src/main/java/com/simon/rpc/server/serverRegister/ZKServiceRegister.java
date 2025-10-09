package com.simon.rpc.server.serverRegister;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.server.serverRegister
 * @Description:
 * @Author: Simon
 * @CreateDate: 2025/10/9
 */
public class ZKServiceRegister implements ServiceRegister {
    // curator 提供的zookeeper客户端
    private CuratorFramework client;
    //zookeeper根路径节点
    private static final String ROOT_PATH = "MyRPC";

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
        System.out.println("zookeeper 连接成功");
    }
    /**
     * 注册服务到注册中心
     * @param serviceName 服务名
     * @param serviceAddress 服务地址
     * */
    @Override
    public void register(String serviceName, InetSocketAddress serviceAddress) {
        try {
            // serviceName创建成永久节点，服务提供者下线时，不删服务名，只删地址
            if(client.checkExists().forPath("/" + serviceName) == null){
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/" + serviceName);
            }
            // 路径地址，一个/代表一个节点
            String path = "/" + serviceName +"/"+ getServiceAddress(serviceAddress);
            // 临时节点，服务端断开连接就自动删除节点
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
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
}
