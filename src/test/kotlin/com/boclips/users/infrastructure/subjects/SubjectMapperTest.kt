package com.boclips.users.infrastructure.subjects

import com.boclips.videos.service.client.Subject
import com.boclips.videos.service.client.VideoServiceClient
import com.boclips.videos.service.client.internal.FakeClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SubjectMapperTest {
    val videoServiceClient: FakeClient = VideoServiceClient.getFakeClient()

    lateinit var subjectMapper: SubjectMapper

    @BeforeEach
    fun setup() {
        subjectMapper = SubjectMapper(videoServiceClient)
        videoServiceClient.clear()
    }

    @Test
    fun `maps id to a name`() {
        videoServiceClient.addSubject(Subject.builder().id("1").name("maths").build())

        assertThat(subjectMapper.getName("1")).isEqualTo("maths")
    }

    @Test
    fun `maps a list of ids to a list of names`() {
        videoServiceClient.addSubject(Subject.builder().id("1").name("maths").build())
        videoServiceClient.addSubject(Subject.builder().id("2").name("science").build())

        assertThat(subjectMapper.getNames(listOf("1", "2"))).contains("maths", "science")
    }

    @Test
    fun `returns empty list of cannot for missing ids`() {
        assertThat(subjectMapper.getNames(listOf("1", "2"))).isEmpty()
    }
}