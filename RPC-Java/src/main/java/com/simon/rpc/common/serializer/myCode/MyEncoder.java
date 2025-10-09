package com.simon.rpc.common.serializer.myCode;

import com.simon.rpc.common.message.MessageType;
import com.simon.rpc.common.message.RpcRequest;
import com.simon.rpc.common.message.RpcResponse;
import com.simon.rpc.common.serializer.mySerializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.common.serializer.myCode
 * @Description: 自定义的序列化编码器
 * @Author: Simon
 * @CreateDate: 2025/10/9
 */
@AllArgsConstructor
//MessageToByteEncoders是netty提供的编码器，用于将消息编码成字节数组，方便网络传输
public class MyEncoder extends MessageToByteEncoder {
    private Serializer serializer;
    /**
     * @Param ctx: 通道处理上下文
     * @Param msg: 要编码的消息
     * @Param out: 输出的字节缓冲区
     * */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        System.out.println(msg.getClass());
        //1.写入消息类型
        if(msg instanceof RpcRequest){
            out.writeShort(MessageType.REQUEST.getCode());
        }
        else if(msg instanceof RpcResponse){
            out.writeShort(MessageType.RESPONSE.getCode());
        }
        //2.写入序列化方式
        out.writeShort(serializer.getType());
        //得到序列化数组
        byte[] serializeBytes = serializer.serialize(msg);
        //3.写入长度
        out.writeInt(serializeBytes.length);
        //4.写入序列化数组
        out.writeBytes(serializeBytes);
    }
}
