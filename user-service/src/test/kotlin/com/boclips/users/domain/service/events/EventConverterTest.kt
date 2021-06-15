package com.boclips.users.domain.service.events

import com.boclips.users.domain.model.feature.Feature
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.OrganisationTag
import com.boclips.users.domain.model.organisation.OrganisationType
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.domain.model.subject.Subject
import com.boclips.users.domain.model.subject.SubjectId
import com.boclips.users.domain.model.user.ExternalIdentity
import com.boclips.users.domain.model.user.ExternalUserId
import com.boclips.users.testsupport.factories.ContentPackageFactory
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.MarketingTrackingFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.deal
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import com.boclips.eventbus.domain.Subject as EventSubject
import com.boclips.eventbus.domain.SubjectId as EventSubjectId

class EventConverterTest {

    @Test
    fun `converting user to event`() {
        val user = UserFactory.sample(
            marketing = MarketingTrackingFactory.sample(
                utmCampaign = "click here",
                utmContent = "OMG you are falling behind your peers. Do you want a better life?",
                utmMedium = "telekinesis",
                utmSource = "AWS",
                utmTerm = "easy"
            ),
            identity = IdentityFactory.sample(
                createdAt = ZonedDateTime.parse("2020-03-20T10:11:12Z")
            ),
            profile = ProfileFactory.sample(
                firstName = "John",
                lastName = "Johnson",
                hasOptedIntoMarketing = false,
                subjects = listOf(
                    Subject(
                        id = SubjectId(
                            "subject-id"
                        ),
                        name = "maths"
                    )
                ),
                role = "PARENT",
                ages = listOf(5, 6, 7, 8),
                school = OrganisationFactory.school(name = "School name")
            ),
            organisation = OrganisationFactory.school(
                features = mapOf(
                    Feature.LTI_AGE_FILTER to false,
                    Feature.LTI_RESPONSIVE_VIDEO_CARD to true
                )
            ),
            externalIdentity = ExternalIdentity(id = ExternalUserId(value = "external-id-1"))
        )

        val eventUser = EventConverter().toEventUser(user)

        assertThat(eventUser.createdAt).isEqualTo("2020-03-20T10:11:12Z")
        assertThat(eventUser.externalUserId).isEqualTo("external-id-1")

        assertThat(eventUser.profile.firstName).isEqualTo("John")
        assertThat(eventUser.profile.lastName).isEqualTo("Johnson")
        assertThat(eventUser.profile.hasOptedIntoMarketing).isEqualTo(false)
        assertThat(eventUser.profile.subjects).containsExactly(EventSubject(EventSubjectId("subject-id"), "maths"))
        assertThat(eventUser.profile.role).isEqualTo("PARENT")
        assertThat(eventUser.profile.ages).containsExactly(5, 6, 7, 8)
        assertThat(eventUser.profile.school?.name).isEqualTo("School name")
        assertThat(eventUser.profile.marketingTracking?.utmCampaign).isEqualTo("click here")
        assertThat(eventUser.profile.marketingTracking?.utmContent).isEqualTo("OMG you are falling behind your peers. Do you want a better life?")
        assertThat(eventUser.profile.marketingTracking?.utmMedium).isEqualTo("telekinesis")
        assertThat(eventUser.profile.marketingTracking?.utmSource).isEqualTo("AWS")
        assertThat(eventUser.profile.marketingTracking?.utmTerm).isEqualTo("easy")

        assertThat(eventUser.organisation.features).isNotNull()
        assertThat(eventUser.organisation.features["LTI_AGE_FILTER"]).isFalse()
        assertThat(eventUser.organisation.features["LTI_RESPONSIVE_VIDEO_CARD"]).isTrue()
    }

    @Test
    fun `convert organisation to event`() {
        val now = ZonedDateTime.now()
        val organisation = OrganisationFactory.district(
            address = Address(
                country = Country.fromCode("USA"),
                state = State.fromCode("IL"),
                postcode = "abc123"
            ),
            deal = deal(
                accessExpiresOn = now,
                billing = true
            ),
            tags = setOf(OrganisationTag.DESIGN_PARTNER)
        )

        val eventOrganisation = EventConverter().toEventOrganisation(organisation)

        assertThat(eventOrganisation.address.countryCode).isEqualTo("USA")
        assertThat(eventOrganisation.countryCode).isEqualTo("USA")
        assertThat(eventOrganisation.address.state).isEqualTo("IL")
        assertThat(eventOrganisation.state).isEqualTo("IL")
        assertThat(eventOrganisation.address.postcode).isEqualTo("abc123")
        assertThat(eventOrganisation.postcode).isEqualTo("abc123")
        assertThat(eventOrganisation.deal.expiresAt).isEqualTo(now)
        assertThat(eventOrganisation.deal.billing).isTrue()
        assertThat(eventOrganisation.tags).containsExactly("DESIGN_PARTNER")
    }

    @Test
    fun `converts organisation's declared features and uses default values for missing ones`() {
        val organisation = OrganisationFactory.school(
            features = mapOf(Feature.LTI_AGE_FILTER to true)
        )

        val eventOrganisation = EventConverter().toEventOrganisation(organisation)

        assertThat(eventOrganisation.features["LTI_AGE_FILTER"]).isTrue()
        assertThat(eventOrganisation.features["LTI_SLS_TERMS_BUTTON"]).isFalse()
        assertThat(eventOrganisation.features["USER_DATA_HIDDEN"]).isFalse()
        assertThat(eventOrganisation.features["BO_WEB_APP_ADDITIONAL_SERVICES"]).isTrue()
    }

    @Test
    fun `convert content package to event`() {
        val contentPackage = ContentPackageFactory.sample(
            id = "content-package-id",
            name = "content package name"
        )

        val event = EventConverter().toEventContentPackage(contentPackage)

        assertThat(event.id.value).isEqualTo("content-package-id")
        assertThat(event.name).isEqualTo("content package name")
    }

    @Test
    fun `convert LTI deployment organisation to event`() {
        val organisation = OrganisationFactory.ltiDeployment(
            tags = setOf(OrganisationTag.DEFAULT_ORGANISATION),
            name = "my-deployment",
            parent = OrganisationFactory.apiIntegration(name = "top-level-lti-organisation")
        )

        val eventOrganisation = EventConverter().toEventOrganisation(organisation)

        assertThat(eventOrganisation.parent?.name).isEqualTo("top-level-lti-organisation")
        assertThat(eventOrganisation.name).isEqualTo("my-deployment")
        assertThat(eventOrganisation.type).isEqualTo(OrganisationType.LTI_DEPLOYMENT.toString())
    }
}
