package com.boclips.users.config

import com.boclips.web.EnableBoclipsApiErrors
import com.fasterxml.jackson.databind.MapperFeature
import org.springframework.context.annotation.Configuration
import org.springframework.hateoas.MediaTypes
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableBoclipsApiErrors
class WebConfig : WebMvcConfigurer {
    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.forEach {
            if (it is MappingJackson2HttpMessageConverter) {
                it.objectMapper.registerModule(Jackson2HalModule())
                it.objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true)
                it.supportedMediaTypes = listOf(MediaTypes.HAL_JSON, MediaTypes.HAL_JSON, MediaType.ALL)
            }
        }
    }
}
