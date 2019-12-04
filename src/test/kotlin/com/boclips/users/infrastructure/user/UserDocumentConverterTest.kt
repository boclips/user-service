package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.UserId
import com.boclips.users.infrastructure.subjects.CacheableSubjectsClient
import com.boclips.users.infrastructure.subjects.VideoServiceSubjectsClient
import com.boclips.users.testsupport.factories.UserDocumentFactory
import com.boclips.videos.service.client.Subject
import com.boclips.videos.service.client.internal.FakeClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

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
        val document = UserDocumentFactory.sample(
            id = "test-id",
            firstName = "Bob",
            lastName = "Gog",
            username = "bob@gog.com",
            email = "bob@gog.com",
            analyticsId = "1233",
            referralCode = "code",
            subjects = listOf("1"),
            ageRange = listOf(1, 2),
            hasOptedIntoMarketing = true,
            marketing = MarketingTrackingDocument(
                utmTerm = "utmTerm",
                utmContent = "utmContent",
                utmMedium = "utmMedium",
                utmSource = "utmSource",
                utmCampaign = "utmCampaign"
            ),
            organisationId = "new-org-id",
            accessExpiresOn = null,
            createdAt = Instant.now()
        )

        val convertedUser = userDocumentConverter.convertToUser(document)

        assertThat(convertedUser.account.id).isEqualTo(UserId("test-id"))
        assertThat(convertedUser.account.username).isEqualTo("bob@gog.com")
        assertThat(convertedUser.account.email).isEqualTo("bob@gog.com")
        assertThat(convertedUser.account.createdAt).isNotNull()
        assertThat(convertedUser.profile!!.firstName).isEqualTo("Bob")
        assertThat(convertedUser.profile!!.lastName).isEqualTo("Gog")
        assertThat(convertedUser.profile!!.ages).containsExactly(1,2)
        assertThat(convertedUser.profile!!.subjects).hasSize(1)
        assertThat(convertedUser.profile!!.subjects[0].name).isEqualTo("maths")
        assertThat(convertedUser.profile!!.hasOptedIntoMarketing).isEqualTo(true)
        assertThat(convertedUser.marketingTracking.utmTerm).isEqualTo("utmTerm")
        assertThat(convertedUser.marketingTracking.utmContent).isEqualTo("utmContent")
        assertThat(convertedUser.marketingTracking.utmMedium).isEqualTo("utmMedium")
        assertThat(convertedUser.marketingTracking.utmSource).isEqualTo("utmSource")
        assertThat(convertedUser.marketingTracking.utmCampaign).isEqualTo("utmCampaign")
        assertThat(convertedUser.organisationAccountId!!.value).isEqualTo("new-org-id")
        assertThat(convertedUser.accessExpiresOn).isNull()
    }

    @Test
    fun `users missing optedIntoMarketing is defaulted to false`() {
        val convertedUser =
            userDocumentConverter.convertToUser(UserDocumentFactory.sample(hasOptedIntoMarketing = null))

        assertThat(convertedUser.profile?.hasOptedIntoMarketing).isFalse()
    }
}
