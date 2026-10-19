package com.durgesh.project.airBnbApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.modelmapper.ModelMapper;

@Configuration
public class MappperConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
