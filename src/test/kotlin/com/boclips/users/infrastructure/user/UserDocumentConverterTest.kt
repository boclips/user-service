package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.infrastructure.subjects.CacheableSubjectsClient
import com.boclips.users.infrastructure.subjects.VideoServiceSubjectsClient
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.MarketingTrackingFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserDocumentFactory
import com.boclips.users.testsupport.factories.UserFactory
import com.boclips.videos.service.client.Subject
import com.boclips.videos.service.client.internal.FakeClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZonedDateTime
import com.boclips.users.domain.model.Subject as SubjectClientType

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
        assertThat(convertedUser.createdAt).isNotNull()
    }

    @Test
    fun `can create document from user, and adds a createdAt date`() {
        val user = UserFactory.sample(
            account = AccountFactory.sample(
                id = "test-id",
                username = "yey@boclips.com"
            ),
            profile = ProfileFactory.sample(
                firstName = "Joseph",
                lastName = "Smith",
                subjects = listOf(SubjectClientType(id = SubjectId("123"), name = "Subject1")),
                ages = listOf(1, 2, 3, 4),
                hasOptedIntoMarketing = true
            ),
            organisationAccountId = OrganisationAccountId("org-id"),
            accessExpiresOn = ZonedDateTime.now().plusDays(1),
            marketing = MarketingTrackingFactory.sample(
                utmTerm = "utmTerm",
                utmContent = "utmContent",
                utmMedium = "utmMedium",
                utmSource = "utmSource",
                utmCampaign = "utmCampaign"
            ),
            analyticsId = AnalyticsId("analytics-id"),
            referralCode = "REFERRAL_CODE"
        )

        val createdAt = Instant.now()
        val userDocument = UserDocument.create(user, createdAt)
        assertThat(userDocument.createdAt).isEqualTo(createdAt)

        assertThat(userDocument.id).isEqualTo("test-id")
        assertThat(userDocument.firstName).isEqualTo("Joseph")
        assertThat(userDocument.lastName).isEqualTo("Smith")
        assertThat(userDocument.email).isEqualTo("yey@boclips.com")
        assertThat(userDocument.username).isEqualTo("yey@boclips.com")
        assertThat(userDocument.subjectIds).contains("123")
        assertThat(userDocument.ageRange).containsExactly(1, 2, 3, 4)
        assertThat(userDocument.analyticsId).isEqualTo("analytics-id")
        assertThat(userDocument.referralCode).isEqualTo("REFERRAL_CODE")
        assertThat(userDocument.hasOptedIntoMarketing).isEqualTo(true)
        assertThat(userDocument.marketing?.utmTerm).isEqualTo("utmTerm")
        assertThat(userDocument.marketing?.utmContent).isEqualTo("utmContent")
        assertThat(userDocument.marketing?.utmMedium).isEqualTo("utmMedium")
        assertThat(userDocument.marketing?.utmSource).isEqualTo("utmSource")
        assertThat(userDocument.marketing?.utmCampaign).isEqualTo("utmCampaign")
        assertThat(userDocument.organisationId).isEqualTo("org-id")
        assertThat(userDocument.accessExpiresOn).isEqualTo(user.accessExpiresOn!!.toInstant())
    }

    @Test
    fun `users createdAt date defaults to null`() {
        val userDocument = UserDocumentFactory.sample(createdAt = null)

        val user = userDocumentConverter.convertToUser(userDocument)

        assertThat(user.createdAt).isNull()
    }

    @Test
    fun `users missing optedIntoMarketing is defaulted to false`() {
        val convertedUser =
            userDocumentConverter.convertToUser(UserDocumentFactory.sample(hasOptedIntoMarketing = null))

        assertThat(convertedUser.profile?.hasOptedIntoMarketing).isFalse()
    }
}
