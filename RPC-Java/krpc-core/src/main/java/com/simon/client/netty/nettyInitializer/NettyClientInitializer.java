package com.simon.client.netty.nettyInitializer;


import com.simon.client.netty.handler.NettyClientHandler;
import com.simon.common.serializer.myCode.MyDecoder;
import com.simon.common.serializer.myCode.MyEncoder;
import com.simon.common.serializer.mySerializer.JsonSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.client.netty.nettyInitializer
 * @Description: 客户端对于Channel的初始化，主要是添加客户端的处理器到Channel的流水线管道中
 * @Author: Simon
 * @CreateDate: 2025/10/8
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    //初始化Channel的流水线处理器
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //使用流水线管道来规定response数据的处理流程
        socketChannel.pipeline()
                .addLast(new MyDecoder())
                //处理Response对象
                .addLast(new NettyClientHandler())
                //编码器，实现自定义序列化
                .addLast(new MyEncoder(new JsonSerializer()));

    }
}