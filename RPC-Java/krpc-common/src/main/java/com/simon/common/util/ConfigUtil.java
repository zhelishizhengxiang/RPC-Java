package com.simon.common.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import lombok.extern.slf4j.Slf4j;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.common.util
 * @Description: 加载配置文件
 * @Author: Simon
 * @CreateDate: 2025/10/17
 */
@Slf4j
public class ConfigUtil {

    /**
     *
     * 加载配置文件，使用默认环境
     * @param targetClass 目标配置类
     * @param prefix 配置前缀
     * @return 配置对象
     */
    public static <T> T loadConfig(Class<T> targetClass, String prefix) {
        return loadConfig(targetClass, prefix, "");
    }

    /**
     *
     * 加载配置文件，支持指定环境
     * @param targetClass 目标配置类
     * @param prefix 配置前缀
     * @param environment 环境标识（如：dev、prod等）
     * @return 配置对象
     */
    public static <T> T loadConfig(Class<T> targetClass, String prefix, String environment) {
        StringBuilder configFileNameBuilder = new StringBuilder("application");

        if (StrUtil.isNotBlank(environment)) {
            configFileNameBuilder.append("-").append(environment);
        }
        configFileNameBuilder.append(".properties");

        // 加载配置文件
        Props properties = new Props(configFileNameBuilder.toString());

        if (properties.isEmpty()) {
            log.warn("配置文件 '{}' 为空或加载失败！", configFileNameBuilder.toString());
        } else {
            log.info("加载配置文件: '{}'", configFileNameBuilder.toString());
        }

        // 返回转化后的配置对象
        try {
            return properties.toBean(targetClass, prefix);
        } catch (Exception e) {
            log.error("配置转换失败，目标类: {}", targetClass.getName(), e);
            throw new RuntimeException("配置加载失败", e);
        }
    }
}
