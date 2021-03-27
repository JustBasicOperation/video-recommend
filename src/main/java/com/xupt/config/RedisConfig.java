package com.xupt.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Slf4j
@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.jedis.pool.max-idle:3}")
    private int maxIdle;

    @Value("${spring.redis.jedis.pool.max-active:8}")
    private int maxTotal;

    @Value("${spring.redis.jedis.pool.max-wait:-1}")
    private long maxWaitMillis;

    @Value("${spring.redis.timeout:6000}")
    private int timeout;

    @Value("${spring.redis.password}")
    private String password;

    @Bean
    public JedisPool initPool() {
        //连接池设定
        JedisPoolConfig config = new JedisPoolConfig();
        //设定最大连接数,注意调用如果使用了多线程  则线程数量应尽可能比maxTotal小，不然性能瓶颈在redis这快
        config.setMaxTotal(maxTotal);
        //设置最大空闲连接数
        config.setMaxIdle(maxIdle);
        //最大的等待时间
        config.setMaxWaitMillis(maxWaitMillis);
        //创建连接池
        return new JedisPool(config, host, port, timeout, password);
    }
}
