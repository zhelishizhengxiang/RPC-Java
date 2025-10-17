package com.simon.consumer;


import com.simon.client.proxy.ClientProxy;
import com.simon.pojo.User;
import com.simon.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.fusesource.jansi.AnsiConsole;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName ConsumerExample
 * @Description 客户端测试
 * @Author Tong
 * @LastChangeDate 2024-12-05 16:20
 * @Version v5.0
 */
@Slf4j
public class ConsumerTest {

    private static final int THREAD_POOL_SIZE = 20;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public static void main(String[] args) throws InterruptedException {
        // 在Windows环境下启用Jansi彩色支持
        AnsiConsole.systemInstall();
        
        try {
            ClientProxy clientProxy = new ClientProxy();
            UserService proxy = clientProxy.getProxy(UserService.class);
            for (int i = 0; i < 120; i++) {
                final Integer i1 = i;
                if (i % 30 == 0) {
                    // Simulate delay for every 30 requests
                    Thread.sleep(10000);
                }

                // Submit tasks to executor service (thread pool)
                executorService.submit(() -> {
                    try {
                        User user = proxy.getUserById(i1);
                        if (user != null) {
                            log.info("从服务端得到的user={}", user);
                        } else {
                            log.warn("获取的 user 为 null, userId={}", i1);
                        }

                        Integer id = proxy.insertUser(User.builder()
                                .id(i1)
                                .userName("User" + i1)
                                .sex(true)
                                .build());

                        if (id != null) {
                            log.info("向服务端插入user的id={}", id);
                        } else {
                            log.warn("插入失败，返回的id为null, userId={}", i1);
                        }
                    } catch (Exception e) {
                        log.error("调用服务时发生异常，userId={}", i1, e);
                    }
                });
            }

            // Gracefully shutdown the executor service
            executorService.shutdown();
        } finally {
            // 程序结束时卸载AnsiConsole
            AnsiConsole.systemUninstall();
        }
    }

}