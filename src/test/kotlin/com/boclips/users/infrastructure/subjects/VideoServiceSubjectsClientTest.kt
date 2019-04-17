package com.boclips.users.infrastructure.subjects

import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.videos.service.client.VideoServiceClient
import com.boclips.videos.service.client.internal.FakeClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class VideoServiceSubjectsClientTest {
    val videoServiceClient: FakeClient = VideoServiceClient.getFakeClient()

    lateinit var videoServiceSubjectsClient: VideoServiceSubjectsClient

    @BeforeEach
    fun setup() {
        videoServiceSubjectsClient = VideoServiceSubjectsClient(videoServiceClient)
        videoServiceClient.clear()
    }

    @Test
    fun `maps subject ids to subjects`() {
        videoServiceClient.addSubject(com.boclips.videos.service.client.Subject.builder().id("1").name("maths").build())

        val subjectIds = listOf(SubjectId(value = "1"))
        assertThat(videoServiceSubjectsClient.getSubjectsById(subjectIds)).contains(
            Subject(
                id = SubjectId(value = "1"),
                name = "maths"
            )
        )
    }

    @Test
    fun `maps a list of ids to a list of names`() {
        videoServiceClient.addSubject(com.boclips.videos.service.client.Subject.builder().id("1").name("maths").build())

        val subjects = videoServiceSubjectsClient.getSubjectsById(listOf(SubjectId(value = "1"), SubjectId(value = "2")))
        assertThat(subjects).hasSize(1)
        assertThat(subjects.first().id.value).isEqualTo("1")
    }

    @Test
    fun `returns empty list of cannot for missing ids`() {
        assertThat(videoServiceSubjectsClient.getSubjectsById(listOf(SubjectId(value = "1")))).isEmpty()
    }
}