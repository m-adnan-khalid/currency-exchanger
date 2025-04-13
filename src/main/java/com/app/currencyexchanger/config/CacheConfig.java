package com.app.currencyexchanger.config;

import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

  public static final String EXCHANGE_RATE_CACHE = "exchangeRateCache";

  @Bean
  public CacheManager ehCacheManager() {
    CachingProvider provider = Caching.getCachingProvider();
    CacheManager cacheManager = provider.getCacheManager();
    // Configure cache with 1 hour TTL
    CacheConfiguration<String, Double> configuration =
        CacheConfigurationBuilder.newCacheConfigurationBuilder(
                String.class,
                Double.class,
                ResourcePoolsBuilder.heap(1000)) // Maximum entries in cache
            .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(
                Duration.ofHours(1))) // TTL of 1 hour
            .build();

    // Create the cache if it doesn't exist
    if (cacheManager.getCache(EXCHANGE_RATE_CACHE, String.class, Double.class) == null) {
      cacheManager.createCache(
          EXCHANGE_RATE_CACHE,
          Eh107Configuration.fromEhcacheCacheConfiguration(configuration));
    }
    return cacheManager;
  }
}