package com.lhb.calculation.calculation.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
public class CacheManagerConfig {


//    @Value("${spring.cache.multi.useRedis}")
//    private Boolean useRedis;
//
//
//    @Bean
//    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory){
//        RedisTemplate<String, String> redisTemplate = new RedisTemplate<String,String>();
//        redisTemplate.setConnectionFactory(factory);
//        // key序列化方式;（不然会出现乱码;）,但是如果方法上有Long等非String类型的话，会报类型转换错误；
//        // 所以在没有自己定义key生成策略的时候，以下这个代码建议不要这么写，可以不配置或者自己实现ObjectRedisSerializer
//        // 或者JdkSerializationRedisSerializer序列化方式;
//        RedisSerializer<String> redisSerializer = new StringRedisSerializer();// Long类型不可以会出现异常信息;
//        redisTemplate.setKeySerializer(redisSerializer);
//        redisTemplate.setValueSerializer(redisSerializer);
//        redisTemplate.setHashKeySerializer(redisSerializer);
//        redisTemplate.setHashValueSerializer(redisSerializer);
//        return redisTemplate;
//    }
//
//    @Bean
//    public CacheManager cacheManager(RedisTemplate<String, String> redisTemplate){
//        CacheManager cacheManager = null;
//        if(useRedis){
//            cacheManager = RedisCacheManager.create(redisTemplate.getRequiredConnectionFactory());
//        }else {
//            cacheManager =  new ConcurrentMapCacheManager();
//        }
//
//        return cacheManager;
//    }


    private final CacheProperties cacheProperties;


    public CacheManagerConfig(CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
    }






    /**
     * cacheManager名字
     */
    public interface CacheManagerNames {
        /**
         * redis
         */
        String REDIS_CACHE_MANAGER = "redisCacheManager";

        /**
         * ehCache
         */
        String EHCACHE_CACHE_MAANGER = "ehCacheCacheManager";
    }

    /**
     * 缓存名，名称暗示了缓存时长 注意： 如果添加了新的缓存名，需要同时在下面的RedisCacheCustomizer#RedisCacheCustomizer里配置名称对应的缓存时长
     * ，时长为0代表永不过期；缓存名最好公司内部唯一，因为可能多个项目共用一个redis。
     *
     */
    public interface RedisCacheNames {
        /** 10秒钟缓存组 */
        String CACHE_10SECENDS = "lhb:cache:15s";
        /** 15分钟缓存组 */
        String CACHE_15MINS = "lhb:cache:15m";
        /** 30分钟缓存组 */
        String CACHE_30MINS = "lhb:cache:30m";
        /** 60分钟缓存组 */
        String CACHE_60MINS = "lhb:cache:60m";
        /** 180分钟缓存组 */
        String CACHE_180MINS = "lhb:cache:180m";
    }

    /**
     * ehcache缓存名
     */
    public interface EhCacheNames {
        String CACHE_10MINS = "lhb:cache:10m";

        String CACHE_20MINS = "lhb:cache:20m";

        String CACHE_30MINS = "lhb:cache:30m";
    }


    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<String,String>();
        redisTemplate.setConnectionFactory(factory);
        // key序列化方式;（不然会出现乱码;）,但是如果方法上有Long等非String类型的话，会报类型转换错误；
        // 所以在没有自己定义key生成策略的时候，以下这个代码建议不要这么写，可以不配置或者自己实现ObjectRedisSerializer
        // 或者JdkSerializationRedisSerializer序列化方式;
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();// Long类型不可以会出现异常信息;
        redisTemplate.setKeySerializer(redisSerializer);
        redisTemplate.setValueSerializer(redisSerializer);
        redisTemplate.setHashKeySerializer(redisSerializer);
        redisTemplate.setHashValueSerializer(redisSerializer);
        return redisTemplate;
    }

    /**
     * 默认的redisCacheManager
     * @param redisTemplate 通过参数注入，这里没有手动给它做配置。在引入了redis的jar包，并且往
     * application.yml里添加了spring.redis的配置项，springboot的autoconfig会自动生成一个
     * redisTemplate的bean
     * @return
     */
    @Bean
    @Primary
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        return new RedisCacheManager(
                RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),
                this.getRedisCacheConfigurationWithTtl(30*60),
                this.getRedisCacheConfigurationMap());
    }

    private Map<String, RedisCacheConfiguration> getRedisCacheConfigurationMap() {
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        //SsoCache和BasicDataCache进行过期时间配置
        redisCacheConfigurationMap.put(RedisCacheNames.CACHE_10SECENDS, this.getRedisCacheConfigurationWithTtl(10));
        redisCacheConfigurationMap.put(RedisCacheNames.CACHE_15MINS, this.getRedisCacheConfigurationWithTtl(15*60));
        redisCacheConfigurationMap.put(RedisCacheNames.CACHE_30MINS, this.getRedisCacheConfigurationWithTtl(15*60));
        redisCacheConfigurationMap.put(RedisCacheNames.CACHE_60MINS, this.getRedisCacheConfigurationWithTtl(15*60));
        redisCacheConfigurationMap.put(RedisCacheNames.CACHE_180MINS, this.getRedisCacheConfigurationWithTtl(15*60));
        return redisCacheConfigurationMap;
    }

    private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(Integer seconds) {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        redisCacheConfiguration = redisCacheConfiguration
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .entryTtl(Duration.ofSeconds(seconds));

        return redisCacheConfiguration;
    }


    /**
     * 创建ehCacheCacheManager
     */
    @Bean
    public EhCacheCacheManager ehCacheCacheManager() {
        Resource location = this.cacheProperties
                .resolveConfigLocation(this.cacheProperties.getEhcache().getConfig());
        return new EhCacheCacheManager(EhCacheManagerUtils.buildCacheManager(location));
    }




}
