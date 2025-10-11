package com.simon.rpc.client.serviceCenter.balance;

import java.util.List;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.client.serviceCenter.balance
 * @Description:
 * @Author: Simon
 * @CreateDate: 2025/10/10
 */
public interface LoadBalance {
    /**
     * 负载均衡算法，根据服务节点列表选择一个节点
     * @param addressList 服务节点列表
     * @return 选中的服务节点
     */
    String balance(List<String> addressList);
     /**
      * 添加一个服务节点
      * @param node 服务节点地址
      */
    void addNode(String node) ;
    /**
     * 删除一个服务节点
     * @param node 服务节点地址
     */
    void delNode(String node);
}
