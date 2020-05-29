package com.boclips.users.domain.service.events

import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.organisation.OrganisationTag
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.domain.model.subject.Subject
import com.boclips.users.domain.model.subject.SubjectId
import com.boclips.users.testsupport.factories.IdentityFactory
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
                        ), name = "maths"
                    )
                ),
                role = "PARENT",
                ages = listOf(5, 6, 7, 8),
                school = OrganisationFactory.school(name = "School name")
            )
        )

        val eventUser = EventConverter().toEventUser(user)

        assertThat(eventUser.createdAt).isEqualTo("2020-03-20T10:11:12Z")
        assertThat(eventUser.profile.firstName).isEqualTo("John")
        assertThat(eventUser.profile.lastName).isEqualTo("Johnson")
        assertThat(eventUser.profile.hasOptedIntoMarketing).isEqualTo(false)
        assertThat(eventUser.profile.subjects).containsExactly(EventSubject(EventSubjectId("subject-id"), "maths"))
        assertThat(eventUser.profile.role).isEqualTo("PARENT")
        assertThat(eventUser.profile.ages).containsExactly(5, 6, 7, 8)
        assertThat(eventUser.profile.school?.name).isEqualTo("School name")
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
}
