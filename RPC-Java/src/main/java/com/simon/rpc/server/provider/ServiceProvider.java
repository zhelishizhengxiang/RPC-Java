package com.simon.rpc.server.provider;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhengx
 * @version 1.0
 * @create 2025/10/7
 * @description 本地服务存放器
 */

public class ServiceProvider {
    //集合中存放所有服务的实例<接口的全限定名，接口对应的实现类实例>
    private Map<String,Object> interfaceProvider;

    public ServiceProvider(){
        this.interfaceProvider=new HashMap<>();
    }

    /**
     * @description 本地注册服务
     * @param service 服务实例
     */
    public void provideServiceInterface(Object service){
        String serviceName=service.getClass().getName();
        Class<?>[] interfaceName=service.getClass().getInterfaces();

        for (Class<?> clazz:interfaceName){
            interfaceProvider.put(clazz.getName(),service);
        }

    }
    //获取服务实例
    public Object getService(String interfaceName){
        return interfaceProvider.get(interfaceName);
    }
}