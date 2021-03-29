package com.riiyn.config;

import feign.Logger;
import feign.codec.Decoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: riiyn
 * @date: 2021/3/28 22:56
 * @description: feign配置类，gateway使用feign时，需要手动注入消息转换器
 */
@Configuration
public class FeignConfig {
    
    @Bean
    Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }
    
    @Bean
    public Decoder feignDecoder(){
        return new ResponseEntityDecoder(new SpringDecoder(httpMessageConvertersObjectFactory()));
    }

    private ObjectFactory<HttpMessageConverters> httpMessageConvertersObjectFactory(){
        final HttpMessageConverters httpMessageConverters = new HttpMessageConverters(new GateWayMappingJackson2HttpMessageConverter());
        return () -> httpMessageConverters;
    }

    private static class GateWayMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
        GateWayMappingJackson2HttpMessageConverter(){
            List<MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"));
            setSupportedMediaTypes(mediaTypes);
        }
    }
}
