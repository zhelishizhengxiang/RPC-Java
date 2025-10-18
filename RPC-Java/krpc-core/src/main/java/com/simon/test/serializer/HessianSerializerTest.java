package com.simon.test.serializer;

import com.simon.common.exception.SerializeException;
import com.simon.common.serializer.mySerializer.HessianSerializer;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.test.balance.serializer
 * @Description: 测试Hessian序列化
 * @Author: Simon
 * @CreateDate: 2025/10/18
 */
public class HessianSerializerTest {
    private HessianSerializer serializer = new HessianSerializer();

    @Test
    public void testSerializeAndDeserialize() {
        // 创建一个测试对象
        String original = "Hello, Hessian!";

        // 序列化
        byte[] serialized = serializer.serialize(original);
        assertNotNull("序列化结果不应为 null", serialized);

        // 反序列化
        Object deserialized = serializer.deserialize(serialized, 3);
        assertNotNull("反序列化结果不应为 null", deserialized);

        // 校验反序列化的结果
        assertEquals("反序列化的对象应该与原对象相同", original, deserialized);
    }

    @Test
    public void testDeserializeWithInvalidData() {
        byte[] invalidData = new byte[]{1, 2, 3}; // 假数据

        // 测试无效数据反序列化
        try {
            serializer.deserialize(invalidData, 3);
            fail("反序列化时应抛出异常");
        } catch (SerializeException e) {
            assertEquals("Deserialization failed", e.getMessage());
        }
    }
}
