package com.boclips.users.infrastructure.subjects

import com.boclips.users.domain.model.subject.Subject
import com.boclips.users.domain.model.subject.SubjectId
import com.boclips.videos.api.httpclient.test.fakes.SubjectsClientFake
import com.boclips.videos.api.response.subject.SubjectResource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class VideoServiceSubjectsClientTest {
    private val subjectsClient = SubjectsClientFake()
    private lateinit var videoServiceSubjectsClient: VideoServiceSubjectsClient

    @BeforeEach
    fun setup() {
        videoServiceSubjectsClient = VideoServiceSubjectsClient(CacheableSubjectsClient(subjectsClient))
        subjectsClient.clear()
    }

    @Test
    fun `maps subject ids to subjects`() {
        subjectsClient.add(SubjectResource(id = "1", name = "French"))

        val subjectIds = listOf(SubjectId(value = "1"))
        assertThat(videoServiceSubjectsClient.getSubjectsById(subjectIds)).contains(
            Subject(
                id = SubjectId(value = "1"),
                name = "French"
            )
        )
    }

    @Test
    fun `maps a list of ids to a list of names`() {
        subjectsClient.add(SubjectResource(id = "1", name = "maths"))

        val subjects =
            videoServiceSubjectsClient.getSubjectsById(listOf(
                SubjectId(
                    value = "1"
                ), SubjectId(value = "2")
            ))
        assertThat(subjects).hasSize(1)
        assertThat(subjects.first().id.value).isEqualTo("1")
    }

    @Test
    fun `returns empty list of cannot for missing ids`() {
        assertThat(videoServiceSubjectsClient.getSubjectsById(listOf(
            SubjectId(
                value = "1"
            )
        ))).isEmpty()
    }

    @Test
    fun `returns true for empty list`() {
        assertThat(videoServiceSubjectsClient.allSubjectsExist(emptyList())).isTrue()
    }

    @Test
    fun `returns true if all are valid in the list`() {
        subjectsClient.add(SubjectResource(id = "1", name = "maths"))
        subjectsClient.add(SubjectResource(id = "2", name = "french"))

        assertThat(
            videoServiceSubjectsClient.allSubjectsExist(listOf(
                SubjectId(
                    value = "1"
                ), SubjectId(value = "2")
            ))
        ).isTrue()
    }

    @Test
    fun `returns false for invalid subject`() {
        assertThat(
            videoServiceSubjectsClient.allSubjectsExist(
                listOf(
                    SubjectId(value = "Invalid")
                )
            )
        ).isFalse()
    }

    @Test
    fun `returns false if any are invalid in the list`() {
        subjectsClient.add(SubjectResource(id = "1", name = "maths"))

        assertThat(
            videoServiceSubjectsClient.allSubjectsExist(
                listOf(
                    SubjectId(value = "1"),
                    SubjectId(value = "Invalid")
                )
            )
        ).isFalse()
    }
}
