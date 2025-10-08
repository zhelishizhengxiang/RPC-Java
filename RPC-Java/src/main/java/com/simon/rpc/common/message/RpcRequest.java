package com.simon.rpc.common.message;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhengx
 * @version 1.0
 * @create 2025/10/7
 *  定义发送的消息格式
 */
@Data
@Builder
public class RpcRequest implements Serializable {
    //服务接口全限定名，客户端只知道接口名，在服务端接口指向实现类
    private String interfaceName;
    //调用的方法名
    private String methodName;
    //参数列表
    private Object[] params;
    //参数类型
    private Class<?>[] paramsType;
}