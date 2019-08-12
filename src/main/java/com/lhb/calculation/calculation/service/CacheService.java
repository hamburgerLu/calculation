package com.lhb.calculation.calculation.service;

import com.lhb.calculation.calculation.configuration.CacheManagerConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CacheService {


    @Cacheable(value = "value")
    public String get(){
        System.out.println("进入service方法啦");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        return "result";
    }



    @Cacheable(key = "#key", cacheManager = CacheManagerConfig.CacheManagerNames.EHCACHE_CACHE_MAANGER, cacheNames = CacheManagerConfig.EhCacheNames.CACHE_10MINS)
    public String demo1(String key) {
        System.out.println("进入了demo1");
        return "abc" + key;
    }

    @Cacheable(key = "#key", cacheNames = CacheManagerConfig.RedisCacheNames.CACHE_10SECENDS)
    public String demo2(String key) {
        System.out.println("进入了demo2");
        return "abc" + key;
    }


}
