package com.simon.rpc.common.message;

import lombok.AllArgsConstructor;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.common.message
 * @Description:消息格式的类型
 * @Author: Simon
 * @CreateDate: 2025/10/9
 */
@AllArgsConstructor
public enum MessageType {
    //枚举常量：代表消息请求
    REQUEST(0),
    //枚举常量：代表消息响应
    RESPONSE(1);
//    每个枚举值对应的编号
    private int code;
    public int getCode(){
        return code;
    }
}
