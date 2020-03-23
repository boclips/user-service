package com.boclips.users.infrastructure.subjects

import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.videos.api.response.subject.SubjectResource
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CacheableSubjectsClientIntegrationTest : AbstractSpringIntegrationTest() {

    @Autowired
    lateinit var cacheableSubjectsClient: CacheableSubjectsClient

    @Test
    fun `it caches subjects`() {
        subjectsClient.add(SubjectResource(id = "1", name = "maths"))
        val cachedSubjects = cacheableSubjectsClient.getSubjects()

        subjectsClient.add(SubjectResource(id = "2", name = "biology"))

        assertThat(cachedSubjects).isEqualTo(cacheableSubjectsClient.getSubjects())
    }

    @Test
    fun `it eventually flushes the cache`() {
        subjectsClient.add(SubjectResource(id = "1", name = "maths"))
        val cachedSubjects = cacheableSubjectsClient.getSubjects()

        subjectsClient.add(SubjectResource(id = "1", name = "biology"))

        await().untilAsserted {
            assertThat(cachedSubjects).isNotEqualTo(cacheableSubjectsClient.getSubjects())
        }
    }
}
