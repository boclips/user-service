package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.SubjectId
import com.boclips.users.infrastructure.subjects.CacheableSubjectsClient
import com.boclips.users.infrastructure.subjects.VideoServiceSubjectsClient
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserDocumentFactory
import com.boclips.users.testsupport.factories.UserFactory
import com.boclips.videos.service.client.Subject
import com.boclips.videos.service.client.internal.FakeClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

class UserDocumentConverterTest {
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
            profile = ProfileFactory.sample(
                subjects = listOf(com.boclips.users.domain.model.Subject(SubjectId("1"), "maths"))
            )
        )

        val convertedUser = userDocumentConverter.convertToUser(UserDocument.from(user))

        assertThat(convertedUser).isEqualTo(user)
    }

    @Test
    fun `can convert document to user with an accessExpiresOn`() {
        val user = UserFactory.sample(
            accessExpiresOn = ZonedDateTime.now(ZoneOffset.UTC)
        )

        val convertedUser = userDocumentConverter.convertToUser(UserDocument.from(user))

        assertThat(convertedUser).isEqualTo(user)
    }

    @Test
    fun `users missing optedIntoMarketing is defaulted to true`() {
        val convertedUser =
            userDocumentConverter.convertToUser(UserDocumentFactory.sample(hasOptedIntoMarketing = null))

        assertThat(convertedUser.profile?.hasOptedIntoMarketing).isFalse()
    }

    @Test
    fun `users without accessExpiresOn have no expiry`() {
        val convertedUser =
            userDocumentConverter.convertToUser(UserDocumentFactory.sample(accessExpiresOn = null))

        assertThat(convertedUser.accessExpiresOn).isNull()
    }

    @Test
    fun `users with accessExpiresOn have expiry`() {
        val accessExpiresOnInstant = Instant.now()
        val convertedUser =
            userDocumentConverter.convertToUser(UserDocumentFactory.sample(accessExpiresOn = accessExpiresOnInstant))

        assertThat(convertedUser.accessExpiresOn?.toInstant()).isEqualTo(accessExpiresOnInstant)
    }
}
