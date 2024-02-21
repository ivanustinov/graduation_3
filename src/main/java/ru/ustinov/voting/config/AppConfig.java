package ru.ustinov.voting.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import ru.ustinov.voting.web.formatter.DateFormatter;
import ru.ustinov.voting.web.json.JsonUtil;

import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

@Configuration
@Slf4j
@EnableCaching
@EnableScheduling
public class AppConfig implements WebMvcConfigurer {

    @Value("file:#{systemEnvironment[VOTING_ROOT]}config/messages/app")
    private String name;

    @Autowired
    private Environment env;

    @Bean
    public TimeZone defaultTimeZone() {
        final TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");
        TimeZone.setDefault(timeZone);
        log.info("Spring boot application running in Moscow timezone :" + LocalTime.now());
        return timeZone;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile("!test")
    Server h2Server() throws SQLException {
        log.info("Start H2 TCP server");
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    //    https://stackoverflow.com/a/46947975/548473
    @Bean
    Module module() {
        return new Hibernate5Module()
                .configure(Hibernate5Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS, true);
    }

    @Bean
    @DependsOn("dateFormatter")
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.simpleDateFormat(DateFormatter.STATIC_ACTIVE_DATE_FORMAT);
            builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(DateFormatter.STATIC_ACTIVE_DATE_FORMAT)));
            builder.deserializers(new LocalDateDeserializer(DateTimeFormatter.ofPattern(DateFormatter.STATIC_ACTIVE_DATE_FORMAT)));
        };
    }

    @Bean
    public DateFormatter dateFormatter() {
        return new DateFormatter(env);
    }


    @Bean
    MessageSource messageSource() {
        final ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        reloadableResourceBundleMessageSource.setCacheSeconds(5);
        reloadableResourceBundleMessageSource.setDefaultEncoding("UTF-8");
        reloadableResourceBundleMessageSource.setBasenames(name);
        reloadableResourceBundleMessageSource.setFallbackToSystemLocale(false);
        return reloadableResourceBundleMessageSource;
    }

    @Bean
    MessageSourceAccessor messageSourceAccessor() {
        return new MessageSourceAccessor(messageSource());
    }

    @Bean
    LocaleContextResolver localeResolver() {
        final CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(new Locale("ru"));
        return cookieLocaleResolver;
    }

    @Bean
    HandlerInterceptor localeChangeInterceptor() {
        final LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    @Autowired
    public void storeObjectMapper(ObjectMapper objectMapper) {
        JsonUtil.setMapper(objectMapper);
    }
}