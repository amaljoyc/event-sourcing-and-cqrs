package com.linusfinance.es.boilerplate.config

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ObjectMapperConfig {

    /*
        setting full visibility in order to use the event sourced aggregate as read model (without CQRS)
        since the properties on the aggregate are private and not accessible
        can be skipped if we rather go with full on CQRS (using a separate projection / read model)
     */
    @Bean
    fun fullVisibilityMapper(): ObjectMapper {
        val mapper = jacksonObjectMapper()
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper
    }
}