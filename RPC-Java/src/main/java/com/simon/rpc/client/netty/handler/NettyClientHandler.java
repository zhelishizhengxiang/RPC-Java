package com.simon.rpc.client.netty.handler;


import com.simon.rpc.common.message.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.client.netty.handler
 * @Description: 客户端的入站处理器，用于处理响应消息
 * @Author: Simon
 * @CreateDate: 2025/10/8
 */
/**
 * SimpleChannelInboundHandler 继承自ChannelInboundHandlerAdapter。
 * 该类拥有泛型，泛型指的是目标消息类，会自动过滤非 T 类型的消息（直接传递给下一个处理器）
 * 只将T 类型的消息传入 channelRead0 方法（注意方法名是 channelRead0，而非 channelRead）
 * */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
        /*AttributeKey 就AttributeKey 是一个用于标识 Channel的一个键，可以在 Channel 的生命周期内安全地存储和共享数据。
        通过 AttributeKey，可以在这个连接的任意处理器（ChannelHandler）中设置、获取、修改与该连接相关的数据
        相当于通过该 AttributeKey 可以在不同的处理器之间传递数据，而不会相互干扰。*/

        //接收到response，给channel起别名，让sendRequest里读取response
        AttributeKey<RpcResponse> attributeKey=AttributeKey.valueOf("rpcResponse");
        //通过attributeKey将response存储到channel中,方便后续逻辑能够通过Channel获取到response
        ctx.channel().attr(attributeKey).set(rpcResponse);
        //调用close方法关闭channel后，只是不继续接收数据了，但是下面的代码会继续执行
        //关闭流程触发的事件方法（如 channelInactive、channelUnregistered）会继续传播到后面的 ChannelHandler
        //但是后面的处理器不会取执行channelRead0方法，因为channel已经关闭了
        ctx.channel().close();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //异常处理
        cause.printStackTrace();
        ctx.close();
    }
}
