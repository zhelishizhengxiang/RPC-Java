package com.simon.common.serializer.mySerializer;

import java.io.*;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.rpc.common.serializer.mySerializer
 * @Description: 使用Java IO对象完成序列化和反序列化
 * @Author: Simon
 * @CreateDate: 2025/10/9
 */
public class ObjectSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        byte[] bytes=null;

        try(ByteArrayOutputStream bos=new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);)
        {
            //是一个对象输出流，用于将 Java 对象序列化为字节流，并将其连接到bos上
            oos.writeObject(obj);
            //刷新 ObjectOutputStream，确保所有缓冲区中的数据都被写入到底层流中。
            oos.flush();
            //将bos其内部缓冲区中的数据转换为字节数组
            bytes = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        Object obj = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis);)
        {
            obj = ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public int getType() {
        return 0;
    }
}
