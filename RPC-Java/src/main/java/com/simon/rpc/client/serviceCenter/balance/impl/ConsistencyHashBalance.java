package com.simon.rpc.client.serviceCenter.balance.impl;

import com.simon.rpc.client.serviceCenter.balance.LoadBalance;

import java.util.*;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.client.serviceCenter.balance.impl
 * @Description: 一致性哈希的负载均衡策略
 * @Author: Simon
 * @CreateDate: 2025/10/10
 */
public class ConsistencyHashBalance implements LoadBalance {
    // 每个物理节点的虚拟节点的个数，
    // 用于增加哈希环上的节点数，提高负载均衡的均匀性
    private static final int VIRTUAL_NUM = 5;

    // 虚拟节点分配，key是hash值，value是虚拟节点服务器名称
    private SortedMap<Integer, String> shards = new TreeMap<>();

    // 真实节点列表
    private List<String> realNodes = new LinkedList<String>();

    //模拟初始服务器
    private String[] servers =null;

    /**
     * 初始化负载均衡器。将真实的服务节点和虚拟节点添加到哈希环之中
     * @param serviceList 服务节点列表
     * */
    private  void init(List<String> serviceList) {
        for (String server :serviceList) {
            realNodes.add(server);
            System.out.println("真实节点[" + server + "] 被添加");
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = server + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.put(hash, virtualNode);
                System.out.println("虚拟节点[" + virtualNode + "] hash:" + hash + "，被添加");
            }
        }
    }
    /**
     * 获取被分配的节点名
     * @param node
     * @return
     */
    public  String getServer(String node,List<String> serviceList) {
        init(serviceList);
        int hash = getHash(node);
        Integer key = null;
        //返回键值大于等于hash的子map，即部分视图
        SortedMap<Integer, String> subMap = shards.tailMap(hash);
        if (subMap.isEmpty()) {
            //如果此时key是最大的，那么按照一致性哈希算法应该是最小的节点
            key = shards.firstKey();
        } else {
            key = subMap.firstKey();
        }
        String virtualNode = shards.get(key);
        return virtualNode.substring(0, virtualNode.indexOf("&&"));
    }

    /**
     * 添加一个真实节点及其虚拟节点到哈希环种
     *
     * @param node
     */
    public  void addNode(String node) {
        if (!realNodes.contains(node)) {
            realNodes.add(node);
            System.out.println("真实节点[" + node + "] 上线添加");
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.put(hash, virtualNode);
                System.out.println("虚拟节点[" + virtualNode + "] hash:" + hash + "，被添加");
            }
        }
    }

    /**
     * 删除真实节点以及其对应的虚拟节点
     * @param node
     */
    public  void delNode(String node) {
        if (realNodes.contains(node)) {
            realNodes.remove(node);
            System.out.println("真实节点[" + node + "] 下线移除");
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.remove(hash);
                System.out.println("虚拟节点[" + virtualNode + "] hash:" + hash + "，被移除");
            }
        }
    }

    /**
     * FNV1_32_HASH算法
     * @param str 给定字符串
     * @return 定长正整数哈希值
     */
    private static int getHash(String str) {
        //该算法中的质数系数
        final int p = 16777619;
        //初始种子值
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        // 如果算出来的值为负数则取其绝对值
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }

    /**
     * 模拟负载均衡，通过生成的一个随机字符串来模拟请求，之后通过一致性哈希选择一个服务器
     * */
    @Override
    public String balance(List<String> addressList) {
        String random= UUID.randomUUID().toString();
        return getServer(random,addressList);
    }
}
