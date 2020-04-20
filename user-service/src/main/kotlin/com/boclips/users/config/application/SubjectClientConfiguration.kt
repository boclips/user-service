package com.boclips.users.config.application

import com.boclips.users.infrastructure.videoservice.VideoServiceProperties
import com.boclips.videos.api.httpclient.SubjectsClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
class SubjectClientConfiguration(
    private val videoServiceProperties: VideoServiceProperties
) {

    @Profile("!test")
    @Bean
    fun subjectsClient() = SubjectsClient.create(apiUrl = videoServiceProperties.baseUrl)
}
