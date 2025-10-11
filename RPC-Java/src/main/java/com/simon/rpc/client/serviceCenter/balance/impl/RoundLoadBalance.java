package com.simon.rpc.client.serviceCenter.balance.impl;

import com.simon.rpc.client.serviceCenter.balance.LoadBalance;

import java.util.List;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.client.serviceCenter.balance.impl
 * @Description: 轮询的负载均衡策略
 * @Author: Simon
 * @CreateDate: 2025/10/10
 */
public class RoundLoadBalance implements LoadBalance {
    //初始值-1表示还未选择过节点
    private int choose=-1;
    @Override
    public String balance(List<String> addressList) {
        choose++;
        choose=choose%addressList.size();
        System.out.println("负载均衡选择了"+choose+"服务器");
        return addressList.get(choose);
    }
    @Override
    public void addNode(String node) {};
    @Override
    public void delNode(String node){};
}
