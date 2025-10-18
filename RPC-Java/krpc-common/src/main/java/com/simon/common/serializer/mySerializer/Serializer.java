package com.simon.common.serializer.mySerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.common.serializer.mySerializer
 * @Description: 提供序列化和反序列化功能的序列化器接口。通过一个静态工厂方法根据类型返回具体的序列化器的实例
 * @Author: Simon
 * @CreateDate: 2025/10/9
 */
public interface Serializer {
    /**
     * 把对象序列化成字节数组,方便网络传输
     * @param obj 要序列化的对象
     * @return 序列化后的字节数组
     */
    byte[] serialize(Object obj);

    /**
     * 从字节数组反序列化成消息, 使用java自带序列化方式不用messageType也能得到相应的对象（序列化字节数组里包含类信息）
     * 其它方式需指定消息格式，再根据message转化成相应的对象
     * @param bytes 序列化后的字节数组
     * @param messageType 消息类型，用于指定反序列化的目标类型
     * @return 反序列化后的对象
     */
    Object deserialize(byte[] bytes, int messageType);

    /**
     * 返回使用的序列器，是哪个。0：java自带序列化方式, 1: json序列化方式
     * @return 序列化器的类型编码
     */
    int getType();

    //使用Map存储序列化器
    // 0：java自带序列化方式, 1: json序列化方式
    static final Map<Integer, Serializer> serializerMap = new HashMap<>();
    /**
     * 根据序列化器的类型编码获取对应的序列化器实例
     * @param code 序列化器的类型编码，0：java自带序列化方式, 1: json序列化方式
     * @return 对应的序列化器实例
     */
    static Serializer getSerializerByCode(int code){
        //只保证初始化一次
        if(serializerMap.isEmpty()){
            serializerMap.put(0, new ObjectSerializer());
            serializerMap.put(1, new JsonSerializer());
            serializerMap.put(2, new KryoSerializer());
            serializerMap.put(3, new HessianSerializer());
            serializerMap.put(4, new ProtostuffSerializer());
        }
        return serializerMap.get(code);
    }



}
