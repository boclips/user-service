package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.user.UserId
import com.boclips.users.infrastructure.subjects.CacheableSubjectsClient
import com.boclips.users.infrastructure.subjects.VideoServiceSubjectsClient
import com.boclips.users.testsupport.factories.OrganisationDocumentFactory
import com.boclips.users.testsupport.factories.UserDocumentFactory
import com.boclips.videos.api.httpclient.test.fakes.SubjectsClientFake
import com.boclips.videos.api.response.subject.SubjectResource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class UserDocumentConverterTest {
    private val subjectsClient = SubjectsClientFake()
    private var userDocumentConverter =
        UserDocumentConverter(VideoServiceSubjectsClient(CacheableSubjectsClient(subjectsClient)))

    @BeforeEach
    internal fun setUp() {
        subjectsClient.clear()
        subjectsClient.add(SubjectResource(id = "1", name = "maths"))
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
            shareCode = "EFG",
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
            organisation = OrganisationDocumentFactory.sample(
                name = "organisation name"
            ),
            accessExpiresOn = null,
            createdAt = Instant.now(),
            role = "TEACHER",
            profileSchool = OrganisationDocumentFactory.sample(name = "School"),
            externalIdentity = ExternalIdentityDocument(id = "external-id-123")
        )

        val convertedUser = userDocumentConverter.convertToUser(document)

        assertThat(convertedUser.identity.id).isEqualTo(UserId("test-id"))
        assertThat(convertedUser.identity.username).isEqualTo("bob@gog.com")
        assertThat(convertedUser.identity.email).isEqualTo("bob@gog.com")
        assertThat(convertedUser.identity.createdAt).isNotNull()
        assertThat(convertedUser.profile!!.firstName).isEqualTo("Bob")
        assertThat(convertedUser.profile!!.lastName).isEqualTo("Gog")
        assertThat(convertedUser.profile!!.ages).containsExactly(1, 2)
        assertThat(convertedUser.profile!!.subjects).hasSize(1)
        assertThat(convertedUser.profile!!.subjects[0].name).isEqualTo("maths")
        assertThat(convertedUser.profile!!.hasOptedIntoMarketing).isEqualTo(true)
        assertThat(convertedUser.profile!!.school?.name).isEqualTo("School")
        assertThat(convertedUser.marketingTracking.utmTerm).isEqualTo("utmTerm")
        assertThat(convertedUser.marketingTracking.utmContent).isEqualTo("utmContent")
        assertThat(convertedUser.marketingTracking.utmMedium).isEqualTo("utmMedium")
        assertThat(convertedUser.marketingTracking.utmSource).isEqualTo("utmSource")
        assertThat(convertedUser.marketingTracking.utmCampaign).isEqualTo("utmCampaign")
        assertThat(convertedUser.organisation?.name).isEqualTo("organisation name")
        assertThat(convertedUser.shareCode).isEqualTo("EFG")
        assertThat(convertedUser.accessExpiresOn).isNull()
        assertThat(convertedUser.profile!!.role).isEqualTo("TEACHER")
        assertThat(convertedUser.externalIdentity!!.id.value).isEqualTo("external-id-123")
    }

    @Test
    fun `users with different usernames and emails are respected`() {
        val document = UserDocumentFactory.sample(
            username = "not a valid email",
            email = "bob@gog.com"
        )

        val convertedUser = userDocumentConverter.convertToUser(document)

        assertThat(convertedUser.identity.username).isEqualTo("not a valid email")
        assertThat(convertedUser.identity.email).isEqualTo("bob@gog.com")
        assertThat(convertedUser.identity.idpEmail).isEqualTo("bob@gog.com")
    }

    @Test
    fun `users missing optedIntoMarketing is defaulted to false`() {
        val convertedUser =
            userDocumentConverter.convertToUser(UserDocumentFactory.sample(hasOptedIntoMarketing = null))

        assertThat(convertedUser.profile?.hasOptedIntoMarketing).isFalse()
    }
}
