package com.simon.config;

import com.simon.client.serviceCenter.balance.impl.ConsistencyHashBalance;
import com.simon.common.serializer.mySerializer.Serializer;
import com.simon.server.serverRegister.ZKServiceRegister;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.common.config
 * @Description:
 * @Author: Simon
 * @CreateDate: 2025/10/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KRpcConfig {
    //名称
    private String name = "krpc";
    //端口
    private Integer port = 9999;
    //主机名
    private String host = "localhost";
    //版本号
    private String version = "1.0.0";
    //注册中心
    private String registry = new ZKServiceRegister().toString();
    //序列化器
    private String serializer = Serializer.getSerializerByCode(3).toString();
    //负载均衡
    private String loadBalance = new ConsistencyHashBalance().toString();
}
