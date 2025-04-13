package com.app.currencyexchanger.config;

import java.time.Duration;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to enable and configure caching using Ehcache.
 * <p>
 * This class sets up the cache manager and defines a cache for storing exchange rates with a TTL
 * (time-to-live) of 1 hour. The cache manager is responsible for managing the caching layer in the
 * application.
 */
@Configuration
@EnableCaching
public class CacheConfig {

  public static final String EXCHANGE_RATE_CACHE = "exchangeRateCache";

  /**
   * Creates and configures the cache manager with specific cache settings.
   * <p>
   * This method configures an Ehcache cache with a TTL of 1 hour for exchange rate data. The cache
   * is stored in heap memory and is limited to 1000 entries. The cache is created only if it
   * doesn't already exist.
   *
   * @return the configured {@link CacheManager} that manages the application's cache
   */
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