package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.SubjectId
import com.boclips.users.infrastructure.subjects.CacheableSubjectsClient
import com.boclips.users.infrastructure.subjects.VideoServiceSubjectsClient
import com.boclips.users.testsupport.factories.MarketingTrackingFactory
import com.boclips.users.testsupport.factories.UserDocumentFactory
import com.boclips.users.testsupport.factories.UserFactory
import com.boclips.videos.service.client.Subject
import com.boclips.videos.service.client.internal.FakeClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserDocumentTest {
    private val fakeClient = FakeClient()
    private var userDocumentConverter =
        UserDocumentConverter(VideoServiceSubjectsClient(CacheableSubjectsClient(fakeClient)))

    @BeforeEach
    internal fun setUp() {
        fakeClient.clear()
        fakeClient.addSubject(Subject.builder().id("1").name("maths").build())
    }

    @Test
    fun `can convert document to user`() {
        val user = UserFactory.sample(
            subjects = listOf(com.boclips.users.domain.model.Subject(id = SubjectId(value = "1"), name = "maths")),
            hasOptedIntoMarketing = true,
            marketing = MarketingTrackingFactory.sample()
        )

        val convertedUser = userDocumentConverter.convertToUser(UserDocument.from(user))

        assertThat(convertedUser).isEqualTo(user)
    }

    @Test
    fun `users missing optedIntoMarketing is defaulted to true`() {
        val convertedUser =
            userDocumentConverter.convertToUser(UserDocumentFactory.sample(hasOptedIntoMarketing = null))

        assertThat(convertedUser.hasOptedIntoMarketing).isTrue()
    }
}