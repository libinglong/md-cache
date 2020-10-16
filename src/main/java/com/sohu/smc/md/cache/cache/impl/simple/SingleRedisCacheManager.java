package com.sohu.smc.md.cache.cache.impl.simple;

import com.sohu.smc.md.cache.cache.impl.CacheSpace;
import com.sohu.smc.md.cache.cache.impl.multidc.RedisCacheManager;
import com.sohu.smc.md.cache.core.Cache;
import com.sohu.smc.md.cache.serializer.PbSerializer;
import com.sohu.smc.md.cache.serializer.Serializer;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.resource.ClientResources;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author binglongli217932
 * <a href="mailto:libinglong9@gmail.com">libinglong:libinglong9@gmail.com</a>
 * @since 2020/10/12
 */
public class SingleRedisCacheManager implements RedisCacheManager, InitializingBean {

    private CacheSpace cacheSpace;
    private RedisClient redisClient;
    private RedisURI redisURI;
    private ClientResources clientResources;
    private Map<String,Cache> cacheMap = new ConcurrentHashMap<>();
    @Setter
    @Getter
    private Serializer serializer;

    public SingleRedisCacheManager(RedisURI redisURI) {
        this.redisURI = redisURI;
    }

    public SingleRedisCacheManager(RedisURI redisURI, ClientResources clientResources) {
        this.redisURI = redisURI;
        this.clientResources = clientResources;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(redisURI,"redisURI can not be null");
        redisClient = initRedisClient(redisURI, clientResources);
        if (serializer == null){
            serializer = new PbSerializer();
        }
        cacheSpace = new SingleCacheSpace(redisClient);
    }

    private RedisClient initRedisClient(RedisURI redisURI,ClientResources clientResources){
        RedisClient redisClient;
        if (clientResources == null){
            redisClient = RedisClient.create(redisURI);
        } else {
            redisClient = RedisClient.create(clientResources, redisURI);
        }
        ClientOptions options = ClientOptions.builder()
                .autoReconnect(true)
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .build();
        redisClient.setOptions(options);
        return redisClient;
    }

    @Override
    @PreDestroy
    public void shutdown(){
        if (redisClient != null){
            redisClient.shutdown();
        }
    }

    @Override
    public Cache getCache(String cacheSpaceName) {
        return cacheMap.computeIfAbsent(cacheSpaceName, cacheSpaceName1 ->
                new SingleRedisCache(cacheSpaceName1, redisClient, cacheSpace, serializer));
    }

}
