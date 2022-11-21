package com.hg.reggie.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author hougen
 * @program Reggie
 * @description redis配置
 * @create 2022-11-21 16:21
 */
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    @Bean
    public RedisTemplate<Object, Object> redisTemplate( RedisConnectionFactory redisConnectionFactory ) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        //默认的key序列化器为JdkSerializationRedisSerializer
        redisTemplate.setKeySerializer( new StringRedisSerializer() );
        redisTemplate.setConnectionFactory( redisConnectionFactory );
        return redisTemplate;
    }
}