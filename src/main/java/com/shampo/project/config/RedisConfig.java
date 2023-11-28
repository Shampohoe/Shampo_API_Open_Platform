package com.shampo.project.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.SingleServerConfig;
import org.redisson.config.TransportMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.redisson.config.Config;
/**
 * ClassName:RedisConfig
 * Package:com.shampo.project.config
 * Description:
 *
 * @Author kkli
 * @Create 2023/11/18 21:19
 * #Version 1.1
 */
@Configuration
public class RedisConfig {
    /**
     * redis template.
     * new GenericToStringSerializer<>(Object.class)
     * @param factory factory
     * @return RedisTemplate
     *///new StringRedisSerializer() new GenericJackson2JsonRedisSerializer()
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        config.setCodec(JsonJacksonCodec.INSTANCE);
        config.setTransportMode(TransportMode.NIO);
        //可以用"rediss://"来启用SSL连接
        singleServerConfig.setAddress("redis://127.0.0.1:6379");
        singleServerConfig.setClientName("shampohoeAPI");
        //singleServerConfig.setPassword("123456");
        singleServerConfig.setDatabase(5);//数据库编号
        singleServerConfig.setIdleConnectionTimeout(10000);//连接空闲超时，单位：毫秒
        singleServerConfig.setConnectTimeout(10000);//连接超时，单位：毫秒
        singleServerConfig.setTimeout(10000);//命令等待超时，单位：毫秒
        //命令失败重试次数,如果尝试达到 retryAttempts（命令失败重试次数） 仍然不能将命令发送至某个指定的节点时，将抛出错误。
        //如果尝试在此限制之内发送成功，则开始启用 timeout（命令等待超时） 计时。
        singleServerConfig.setRetryAttempts(3);
        singleServerConfig.setRetryInterval(4000);//命令重试发送时间间隔，单位：毫秒
        singleServerConfig.setSubscriptionsPerConnection(5);//单个连接最大订阅数量
        singleServerConfig.setSubscriptionConnectionPoolSize(500);//发布和订阅连接池大小
        singleServerConfig.setSubscriptionConnectionMinimumIdleSize(5);//发布和订阅连接的最小空闲连接数
        singleServerConfig.setConnectionMinimumIdleSize(32);//最小空闲连接数
        singleServerConfig.setConnectionPoolSize(128);//连接池大小
        singleServerConfig.setDnsMonitoringInterval(5000);// DNS监测时间间隔，单位：毫秒
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }


}
