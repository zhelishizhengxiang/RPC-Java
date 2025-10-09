package com.simon.rpc.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.client.cache
 * @Description:  客户端缓存的服务内容
 * @Author: Simon
 * @CreateDate: 2025/10/9
 */
public class ServiceCache {
    //<服务名，服务提供者名单>，同一个可能有多个服务器去提供
    private static Map<String, List<String>> cache=new HashMap<>();

    /**
     * 添加服务到缓存中
     * @param serviceName 服务名
     * @param address 服务提供者地址
     * */
    public void addServiceToCache(String serviceName,String address){
        if(cache.containsKey(serviceName)){
            List<String> addressList = cache.get(serviceName);
            addressList.add(address);
            System.out.println("将name为"+serviceName+"和地址为"+address+"的服务添加到本地缓存中");
        }else {
            List<String> addressList=new ArrayList<>();
            addressList.add(address);
            cache.put(serviceName,addressList);
        }
    }
    /**
     * 修改服务地址
     * @param serviceName 服务名
     * @param oldAddress 旧地址
     * @param newAddress 新地址
     * */
    public void replaceServiceAddress(String serviceName,String oldAddress,String newAddress){
        if(cache.containsKey(serviceName)){
            List<String> addressList=cache.get(serviceName);
            addressList.remove(oldAddress);
            addressList.add(newAddress);
        }else {
            System.out.println("修改失败，服务不存在");
        }
    }
    /**
     * 从缓存中取服务地址
     * @param serviceName 服务名
     * @return 服务提供者地址列表
     * */
    public  List<String> getServiceFromCache(String serviceName){
        if(!cache.containsKey(serviceName)) {
            return null;
        }
        List<String> addressList=cache.get(serviceName);
        System.out.println("从本地缓存获得name为"+serviceName+"的服务提供者地址列表"+addressList);
        return addressList;
    }
    /**
     * 从缓存中删除服务地址
     * @param serviceName 服务名
     * @param address 服务提供者地址
     * */
    public void deleteServiceFromCache(String serviceName,String address){
        List<String> addressList = cache.get(serviceName);
        addressList.remove(address);
        System.out.println("将name为"+serviceName+"和地址为"+address+"的服务从本地缓存中删除");
    }
}
