package com.simon.rpc.server.provider;

import com.simon.rpc.server.serverRegister.ServiceRegister;
import com.simon.rpc.server.serverRegister.ZKServiceRegister;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhengx
 * @version 1.0
 * @create 2025/10/7
 * @description 本地服务存放器
 */

public class ServiceProvider {
    private int port;
    private String host;
    //集合中存放所有服务的实例<接口的全限定名，接口对应的实现类实例>
    private Map<String,Object> interfaceProvider;
    //注册中心
    private ServiceRegister serviceRegister;

    public ServiceProvider(int  port, String host) {
        //传入服务端自身的网络地址
        this.port=port;
        this.host=host;
        this.interfaceProvider=new HashMap<>();
        this.serviceRegister=new ZKServiceRegister();
    }

    /**
     * @description 本地注册服务
     * @param service 服务实例
     */
    public void provideServiceInterface(Object service){
        String serviceName=service.getClass().getName();
        Class<?>[] interfaceName=service.getClass().getInterfaces();

        for (Class<?> clazz:interfaceName){
            //将服务注册到注册中心
            serviceRegister.register(clazz.getName(),new InetSocketAddress(host,port));
            interfaceProvider.put(clazz.getName(),service);
        }

    }
    //获取服务实例
    public Object getService(String interfaceName){
        return interfaceProvider.get(interfaceName);
    }
}