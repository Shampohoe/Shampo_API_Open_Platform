package com.shampo.project.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;


/**
 * ClassName:UserInterfaceInfoServiceTest
 * Package:com.shampo.project.service
 * Description:
 *
 * @Author kkli
 * @Create 2023/10/29 14:44
 * #Version 1.1
 */
@SpringBootTest
public class UserInterfaceInfoServiceTest {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;
    @Resource
    private RedissonClient redissonClient;
    @Test
    public void invokeCount() throws InterruptedException {
        RLock lock = redissonClient.getLock("my-lock");

        // 加锁
        lock.lock();// 阻塞式等待。默认加的锁都是30s时间。
        try {
            System.out.println("加锁成功，执行业务..." + Thread.currentThread().getId());
            Thread.sleep(20000);
        } catch (Exception e) {

        } finally {
            // 解锁
            System.out.println("释放锁..." + Thread.currentThread().getId());
            lock.unlock();
        }

    }
}