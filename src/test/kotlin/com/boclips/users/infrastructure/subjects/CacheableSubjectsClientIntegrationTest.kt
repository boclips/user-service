package com.boclips.users.infrastructure.subjects

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.videos.service.client.internal.FakeClient
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CacheableSubjectsClientIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var videoServiceClient: FakeClient

    @Autowired
    lateinit var cacheableSubjectsClient: CacheableSubjectsClient

    @Test
    fun `it caches subjects`() {
        videoServiceClient.addSubject(com.boclips.videos.service.client.Subject.builder().id("1").build())
        val cachedSubjects = cacheableSubjectsClient.getSubjects()

        videoServiceClient.addSubject(com.boclips.videos.service.client.Subject.builder().id("2").build())

        assertThat(cachedSubjects).isEqualTo(cacheableSubjectsClient.getSubjects())
    }

    @Test
    fun `it eventually flushes the cache`() {
        videoServiceClient.addSubject(com.boclips.videos.service.client.Subject.builder().id("1").build())
        val cachedSubjects = cacheableSubjectsClient.getSubjects()

        videoServiceClient.addSubject(com.boclips.videos.service.client.Subject.builder().id("2").build())

        await().untilAsserted {
            assertThat(cachedSubjects).isNotEqualTo(cacheableSubjectsClient.getSubjects())
        }
    }
}