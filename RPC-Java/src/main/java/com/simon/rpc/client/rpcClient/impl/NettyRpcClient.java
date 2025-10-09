package com.simon.rpc.client.rpcClient.impl;


import com.simon.rpc.client.netty.nettyInitializer.NettyClientInitializer;
import com.simon.rpc.client.rpcClient.RpcClient;
import com.simon.rpc.client.serviceCenter.ServiceCenter;
import com.simon.rpc.client.serviceCenter.ZKServiceCenter;
import com.simon.rpc.common.message.RpcRequest;
import com.simon.rpc.common.message.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.client.rpcClient.impl
 * @ClassName: NettyRpcClient
 * @Description: netty后的客户端类。
 * @Author: Simon
 * @CreateDate: 2025/10/8
 */
public class NettyRpcClient implements RpcClient {
    private ServiceCenter  serviceCenter;
    //netty启动引导类bootstrap
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;
    public NettyRpcClient() throws InterruptedException {
        this.serviceCenter=new ZKServiceCenter();
    }
    //netty客户端初始化
    static {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                 .channel(NioSocketChannel.class)
                 //配置客户端对消息的处理流程
                 .handler(new NettyClientInitializer());
    }
    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        //先从注册中心拿到服务地址
        InetSocketAddress serverAddress = serviceCenter.serviceDiscovery(request.getInterfaceName());
        String host = serverAddress.getHostString();
        int port = serverAddress.getPort();
        try {
            //创建一个channelFuture对象，代表这一个操作事件，sync方法表示堵塞直到connect完成
            ChannelFuture channelFuture  = bootstrap.connect(host, port).sync();
            //获取连接后的Channel，后续的操作都基于channel
            Channel channel = channelFuture.channel();
            // 发送数据
            channel.writeAndFlush(request);
            //关闭channel并阻塞等待关闭完成，底层会等待接收到数据之后并由底层handler来关闭
            channel.closeFuture().sync();
            // 阻塞的获得结果，通过给channel设计别名，获取特定名字下的channel中的内容（handler中设置）
            // AttributeKey是，线程隔离的，不会由线程安全问题。
            // 其它场景也可以选择添加监听器的方式来异步获取结果 channelFuture.addListener...
            //Attribute的数据生命周期与channel对象本身绑定：
            // 即使channel的网络连接已关闭，只要channel对象还没有被JVM垃圾回收，就可以访问其Attribute中存储的数据
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            RpcResponse response = channel.attr(key).get();
            System.out.println(response);
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}