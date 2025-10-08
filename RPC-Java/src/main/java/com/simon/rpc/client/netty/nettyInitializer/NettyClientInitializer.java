package com.simon.rpc.client.netty.nettyInitializer;

import com.simon.rpc.client.netty.handler.NettyClientHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

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
                //首先使用解码器解析的自定义消息格式：长度+消息体的方式来解决粘包问题
                /**
                 * 1. 最大帧长度：Integer.MAX_VALUE
                 * 2. 长度FieldOffset：0，因为长度字段在消息体的开始位置
                 * 3. 长度FieldLength：长度字段本身的字节数
                 * 4. 长度Adjustment：长度字段的值 ≠ 内容真实长度”，需用此参数修正。该消息格式的长度值指就是消息体的长度，所以为0
                 * 5. InitialBytestoStrip：解析不需要保留 “长度字段、魔数” 等头部，可通过此参数剥离。此处玻璃前面的长度字段
                 */
                .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                //对象解码器，转换为Response对象
                .addLast(new ObjectDecoder(new ClassResolver() {
                    @Override
                    public Class<?> resolve(String s) throws ClassNotFoundException {
                        return Class.forName(s);
                    }
                }))
                //处理Response对象
                .addLast(new NettyClientHandler())
                //编码器，对已经编码后的字节流” 的数据头部添加一个 int 类型（4 字节）的长度字段，值为 “后续实际数据的字节数”
                .addLast(new LengthFieldPrepender(4))
                //编码器，序列化成字节流
                .addLast(new ObjectEncoder());

    }
}