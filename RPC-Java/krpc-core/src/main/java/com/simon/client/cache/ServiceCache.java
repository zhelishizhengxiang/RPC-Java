package com.simon.client.cache;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.client.cache
 * @Description:  客户端缓存的服务内容
 * @Author: Simon
 * @CreateDate: 2025/10/9
 */
@Slf4j
public class ServiceCache {
    //<服务名，服务提供者名单>，同一个可能有多个服务器去提供
    private static Map<String, List<String>> cache=new ConcurrentHashMap<>();

    /**
     * 添加服务到缓存中
     * @param serviceName 服务名
     * @param address 服务提供者地址
     * */
    public void addServiceToCache(String serviceName,String address){
        if(cache.containsKey(serviceName)){
            List<String> addressList = cache.get(serviceName);
            addressList.add(address);
            log.info("有服务名情况，将name为{}和地址为{}的服务添加到本地缓存中", serviceName, address);
        }else {
            List<String> addressList=new ArrayList<>();
            addressList.add(address);
            cache.put(serviceName,addressList);
            log.info("无服务名情况，将name为{}和地址为{}的服务添加到本地缓存中", serviceName, address);
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
            log.info("将服务{}的地址{}替换为{}", serviceName, oldAddress, newAddress);
        }else {
            log.error("旧地址{}不在服务{}的地址列表中", oldAddress, serviceName);
        }
    }
    /**
     * 从缓存中取服务地址
     * @param serviceName 服务名
     * @return 服务提供者地址列表
     * */
    public  List<String> getServiceFromCache(String serviceName){
        if(!cache.containsKey(serviceName)) {
            log.warn("服务{}未找到", serviceName);
            //返回个不可修改的空列表，避免调用的时候出现空指针异常
            return Collections.emptyList();
        }

       return cache.get(serviceName);
    }
    /**
     * 从缓存中删除服务地址
     * @param serviceName 服务名
     * @param address 服务提供者地址
     * */
    public void deleteServiceFromCache(String serviceName,String address){
        List<String> addressList = cache.get(serviceName);
        if (addressList != null && addressList.contains(address)) {
            addressList.remove(address);
            log.info("将name为{}和地址为{}的服务从本地缓存中删除", serviceName, address);
            if (addressList.isEmpty()) {
                cache.remove(serviceName);
                log.info("服务{}的地址列表为空，已从缓存中清除", serviceName);
            }
        } else {
            log.warn("删除失败，地址{}不在服务{}的地址列表中", address, serviceName);
        }
    }
}
