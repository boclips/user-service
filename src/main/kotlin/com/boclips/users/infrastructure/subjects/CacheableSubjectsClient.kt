package com.boclips.users.infrastructure.subjects

import com.boclips.videos.service.client.VideoServiceClient
import mu.KLogging
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.scheduling.annotation.Scheduled

open class CacheableSubjectsClient(val videoServiceClient: VideoServiceClient) {
    companion object : KLogging()

    @Cacheable("subjects")
    open fun getSubjects() = videoServiceClient.subjects

    @CacheEvict("subjects")
    @Scheduled(fixedDelayString = "\${subjects.cache.timetolive}")
    open fun flushSubjectsCache() {
        logger.info { "Flushing subjects cache" }
    }
}