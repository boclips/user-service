package com.boclips.users.infrastructure

import com.boclips.videos.service.client.Subject
import com.boclips.videos.service.client.VideoServiceClient
import com.boclips.videos.service.client.internal.FakeClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SubjectValidatorTest {
    val videoServiceClient: FakeClient = VideoServiceClient.getFakeClient()

    lateinit var subjectValidator: SubjectValidator

    @BeforeEach
    fun setup() {
        subjectValidator = SubjectValidator(videoServiceClient)
        videoServiceClient.clear()
    }

    @Test
    fun `returns true for empty list`() {
        assertThat(subjectValidator.isValid(emptyList())).isTrue()
    }

    @Test
    fun `returns true if all are valid in the list`() {
        videoServiceClient.addSubject(Subject.builder().id("1").build())
        videoServiceClient.addSubject(Subject.builder().id("2").build())

        assertThat(subjectValidator.isValid(listOf("1", "2"))).isTrue()
    }

    @Test
    fun `returns false for invalid subject`() {
        assertThat(subjectValidator.isValid(listOf("Invalid"))).isFalse()
    }

    @Test
    fun `returns false if any are invalid in the list`() {
        videoServiceClient.addSubject(Subject.builder().id("1").build())

        assertThat(subjectValidator.isValid(listOf("1", "Invalid"))).isFalse()
    }
}