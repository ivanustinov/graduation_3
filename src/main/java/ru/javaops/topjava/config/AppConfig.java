package ru.javaops.topjava.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.javaops.topjava.web.json.JsonUtil;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
@EnableCaching
public class AppConfig {

//    @Bean
//    public CaffeineCache res() {
//        return new CaffeineCache("res",
//                Caffeine.newBuilder()
//                        .expireAfterAccess(30, TimeUnit.MINUTES)
//                        .maximumSize(200)
//                        .recordStats()
//                        .build());
//    }
//
//    @Bean
//    public CaffeineCache votes() {
//        return new CaffeineCache("votes",
//                Caffeine.newBuilder()
//                        .expireAfterAccess(60, TimeUnit.MINUTES)
//                        .recordStats()
//                        .build());
//    }
//
//    @Bean
//    public CaffeineCache users() {
//        return new CaffeineCache("users",
//                Caffeine.newBuilder()
//                        .expireAfterAccess(60, TimeUnit.MINUTES)
//                        .recordStats()
//                        .build());
//    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile("!test")
    Server h2Server() throws SQLException {
        log.info("Start H2 TCP server");
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }

    //    https://stackoverflow.com/a/46947975/548473
    @Bean
    Module module() {
        return new Hibernate5Module();
    }


    @Autowired
    public void storeObjectMapper(ObjectMapper objectMapper) {
        JsonUtil.setMapper(objectMapper);
    }
}