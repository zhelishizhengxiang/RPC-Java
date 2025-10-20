package com.simon.server.netty.nettyInitializer;


import com.simon.common.serializer.myCode.MyDecoder;
import com.simon.common.serializer.myCode.MyEncoder;
import com.simon.common.serializer.mySerializer.JsonSerializer;
import com.simon.server.netty.handler.NettyServerHandler;
import com.simon.server.provider.ServiceProvider;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.server.netty.nettyInitializer
 * @Description:
 * @Author: Simon
 * @CreateDate: 2025/10/8
 */
@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    private ServiceProvider serviceProvider;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //使用流水线管道来规定response数据的处理流程
        socketChannel.pipeline()
                .addLast(new MyDecoder())
                //处理Request对象
                .addLast(new NettyServerHandler(serviceProvider))
                //编码器，实现自定义逻辑
                .addLast(new MyEncoder(new JsonSerializer()));

    }
}