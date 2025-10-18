package com.simon.consumer;


import com.simon.common.util.ConfigUtil;
import com.simon.config.KRpcConfig;

/**
 * @ClassName ConsumerTestConfig
 * @Description 测试配置顶
 * @Author Simon
 * @LastChangeDate 2024-12-05 11:29
 * @Version v1.0
 */
public class ConsumerTestConfig {
    public static void main(String[] args) {
        KRpcConfig rpc = ConfigUtil.loadConfig(KRpcConfig.class, "rpc");
        System.out.println(rpc);
    }

}
