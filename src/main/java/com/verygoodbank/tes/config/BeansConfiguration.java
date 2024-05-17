package com.verygoodbank.tes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

@Configuration
public class BeansConfiguration {

    @Bean
    public DefaultDataBufferFactory bufferFactory() {
        return new DefaultDataBufferFactory();
    }

}
