package com.simon.server.server.impl;


import com.simon.server.netty.nettyInitializer.NettyServerInitializer;
import com.simon.server.provider.ServiceProvider;
import com.simon.server.server.RpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.Server.server.impl
 * @Description:
 * @Author: Simon
 * @CreateDate: 2025/10/8
 */

@Slf4j
@AllArgsConstructor
public class NettyRpcServer implements RpcServer {
    private ServiceProvider serviceProvider;
    // ChannelFuture 在 start 方法内初始化
    private ChannelFuture channelFuture;
    public NettyRpcServer(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }
    @Override
    public void start(int port) {
        // netty 服务线程组boss负责建立连接， work负责具体的请求
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        log.info("netty服务端启动了");
        try {
            //启动netty服务器引导类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //初始化
            serverBootstrap.
                    group(bossGroup,workGroup).
                    channel(NioServerSocketChannel.class)
                    //NettyClientInitializer这里 配置netty对消息的处理机制
                    .childHandler(new NettyServerInitializer(serviceProvider));
            //绑定端口，同步堵塞,等待启动完成
            channelFuture=serverBootstrap.bind(port).sync();
            //阻塞等待服务关闭
            channelFuture.channel().closeFuture().sync();
        }catch (InterruptedException e){
            log.error("Netty服务端启动中断：{}", e.getMessage(), e);
        } finally {
            // 集中管理线程组资源
            shutdown(bossGroup, workGroup);
            log.info("Netty服务端关闭");
        }
    }

    @Override
    public void stop() {
        if (channelFuture != null) {
            try {
                channelFuture.channel().close().sync();
                log.info("Netty服务端主通道已关闭");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("关闭Netty服务端主通道时中断：{}", e.getMessage(), e);
            }
        } else {
            log.warn("Netty服务端主通道尚未启动，无法关闭");
        }
    }

    private void shutdown(NioEventLoopGroup bossGroup, NioEventLoopGroup workGroup) {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully().syncUninterruptibly();
        }
        if (workGroup != null) {
            workGroup.shutdownGracefully().syncUninterruptibly();
        }
    }
}