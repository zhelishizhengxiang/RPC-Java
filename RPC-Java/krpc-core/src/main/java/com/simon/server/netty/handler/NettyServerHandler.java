package com.simon.server.netty.handler;

import com.simon.common.message.RpcRequest;
import com.simon.common.message.RpcResponse;
import com.simon.server.provider.ServiceProvider;
import com.simon.server.ratelimit.RateLimit;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.Server.netty.handler
 * @Description:
 * @Author: Simon
 * @CreateDate: 2025/10/8
 */

@AllArgsConstructor
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private ServiceProvider serviceProvide;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        if (rpcRequest == null) {
            log.error("接收到非法请求，RpcRequest 为空");
            return;
        }
        //接收request，读取并调用服务
        RpcResponse response= getResponse(rpcRequest);
        //通过监听器确保数据发送完成后再关闭连接
        ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private RpcResponse getResponse(RpcRequest rpcRequest){
        //得到服务名
        String interfaceName=rpcRequest.getInterfaceName();
        //接口服务限流降级
        RateLimit rateLimit=serviceProvide.getRateLimitProvider().getRateLimit(rpcRequest.getInterfaceName());
        if(!rateLimit.getToken()){
            //如果获取令牌失败，则进行限流降级，快速返回结果
            log.warn("服务限流，接口: {}", interfaceName);
            return RpcResponse.fail("服务限流，接口 " + interfaceName + " 当前无法处理请求。请稍后再试。");
        }
        //得到服务端相应服务实现类
        Object service = serviceProvide.getService(interfaceName);
        //反射调用方法
        Method method;
        try {
            method= service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamsType());
            Object data=method.invoke(service,rpcRequest.getParams());
            return RpcResponse.sussess(data);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("方法执行错误，接口: {}, 方法: {}", interfaceName, rpcRequest.getMethodName(), e);
            return RpcResponse.fail("方法执行错误");
        }
    }
}