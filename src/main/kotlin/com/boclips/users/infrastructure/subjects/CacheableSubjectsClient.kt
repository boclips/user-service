package com.boclips.users.infrastructure.subjects

import com.boclips.videos.api.httpclient.SubjectsClient
import mu.KLogging
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.scheduling.annotation.Scheduled

open class CacheableSubjectsClient(private val subjectsClient: SubjectsClient) {
    companion object : KLogging()

    @Cacheable("subjects")
    open fun getSubjects() = subjectsClient.getSubjects()

    @CacheEvict("subjects")
    @Scheduled(fixedDelayString = "\${subjects.cache.timetolive}")
    open fun flushSubjectsCache() {
    }
}
