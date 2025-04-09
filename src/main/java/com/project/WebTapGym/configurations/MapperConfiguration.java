package com.project.WebTapGym.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.modelmapper.ModelMapper;

@Configuration
public class MapperConfiguration {
    @Bean
    public ModelMapper modelMapper()
    {
        return new ModelMapper();
    }
}
