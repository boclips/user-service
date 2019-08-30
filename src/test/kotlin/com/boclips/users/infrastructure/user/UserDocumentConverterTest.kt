package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.OrganisationType
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.infrastructure.subjects.CacheableSubjectsClient
import com.boclips.users.infrastructure.subjects.VideoServiceSubjectsClient
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserDocumentFactory
import com.boclips.users.testsupport.factories.UserFactory
import com.boclips.videos.service.client.Subject
import com.boclips.videos.service.client.internal.FakeClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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
    fun `can convert boclips user`() {
        val user = UserFactory.sample(organisationType = OrganisationType.BoclipsForTeachers)

        val convertedUser = userDocumentConverter.convertToUser(UserDocument.from(user))
        assertThat(convertedUser.organisationType).isEqualTo(OrganisationType.BoclipsForTeachers)
    }

    @Test
    fun `can convert api client`() {
        val user = UserFactory.sample(organisationType = OrganisationType.ApiCustomer(organisationId = OrganisationId("test")))

        val convertedUser = userDocumentConverter.convertToUser(UserDocument.from(user))

        assertThat(convertedUser.organisationType).isEqualTo(OrganisationType.ApiCustomer(organisationId = OrganisationId("test")))
    }

    @Test
    fun `users missing optedIntoMarketing is defaulted to true`() {
        val convertedUser =
            userDocumentConverter.convertToUser(UserDocumentFactory.sample(hasOptedIntoMarketing = null))

        assertThat(convertedUser.profile?.hasOptedIntoMarketing).isFalse()
    }
}
