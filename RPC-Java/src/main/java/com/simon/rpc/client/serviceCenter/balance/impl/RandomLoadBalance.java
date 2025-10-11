package com.simon.rpc.client.serviceCenter.balance.impl;

import com.simon.rpc.client.serviceCenter.balance.LoadBalance;

import java.util.List;
import java.util.Random;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.client.serviceCenter.balance.impl
 * @Description: 随机的负载均衡策略
 * @Author: Simon
 * @CreateDate: 2025/10/10
 */
public class RandomLoadBalance implements LoadBalance {
    @Override
    public String balance(List<String> addressList) {
        Random random=new Random();
        int choose = random.nextInt(addressList.size());
        System.out.println("负载均衡选择了"+choose+"服务器");
        return addressList.get(choose);
    }
    public void addNode(String node){} ;
    public void delNode(String node){};
}
