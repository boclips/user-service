package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.service.UserUpdate
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.MarketingTrackingFactory
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.TeacherPlatformAttributesFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZoneOffset
import java.time.ZonedDateTime

class MongoUserRepositoryTest : AbstractSpringIntegrationTest() {

    @Test
    fun `persists user`() {
        val user = UserFactory.sample(
            marketing = MarketingTrackingFactory.sample(
                utmCampaign = "campaign",
                utmSource = "source",
                utmMedium = "medium",
                utmContent = "content",
                utmTerm = "term"
            ),
            referralCode = "referral-123",
            analyticsId = AnalyticsId(value = "analytics-123")
        )

        userRepository.create(user)

        val fetchedUser = userRepository.findById(user.id)

        assertThat(fetchedUser).isNotNull()

        assertThat(fetchedUser!!.id).isEqualTo(user.id)
        assertThat(fetchedUser.referralCode).isEqualTo(user.referralCode)
    }

    @Test
    fun `can get all accounts`() {
        val savedUsers = listOf(
            userRepository.create(UserFactory.sample("id-1")),
            userRepository.create(UserFactory.sample("id-2"))
        )

        assertThat(userRepository.findAll(savedUsers.map { it.id })).containsAll(savedUsers)
    }

    @Test
    fun `updating user first name field only replaces first name`() {
        val user = userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(
                    id = "user-1"
                ),
                profile = ProfileFactory.sample(firstName = "Ada", lastName = "Lovelace")
            )
        )

        userRepository.update(user, UserUpdate.ReplaceFirstName("Amelia"))

        val updatedUser = userRepository.findById(user.id)!!

        assertThat(updatedUser.profile!!.firstName).isEqualTo("Amelia")
        assertThat(updatedUser.profile!!.lastName).isEqualTo("Lovelace")
    }

    @Test
    fun `updating multiple fields`() {
        val user = userRepository.create(
            UserFactory.sample(
                profile = ProfileFactory.sample(
                    firstName = "Ada",
                    lastName = "Lovelace",
                    hasOptedIntoMarketing = false
                ),
                referralCode = "",
                organisationId = null
            )
        )

        userRepository.update(
            user,
            UserUpdate.ReplaceLastName("Earhart"),
            UserUpdate.ReplaceHasOptedIntoMarketing(true),
            UserUpdate.ReplaceReferralCode("1234"),
            UserUpdate.ReplaceRole(role = "TEACHER")
        )

        val updatedUser = userRepository.findById(user.id)!!

        assertThat(updatedUser.profile!!.lastName).isEqualTo("Earhart")
        assertThat(updatedUser.profile!!.hasOptedIntoMarketing).isEqualTo(true)
        assertThat(updatedUser.referralCode).isEqualTo("1234")
        assertThat(updatedUser.profile!!.role).isEqualTo("TEACHER")
    }

    @Test
    fun `updating user organisation`() {
        val originalOrganisation = OrganisationFactory.school(
            id = OrganisationId()
        )
        val user = userRepository.create(
            UserFactory.sample(
                organisation = originalOrganisation
            )
        )

        val newOrganisation = OrganisationFactory.school(
            id = OrganisationId(),
            school = OrganisationDetailsFactory.school(
                district = OrganisationFactory.district()
            )
        )

        val updatedUser = userRepository.update(user, UserUpdate.ReplaceOrganisation(newOrganisation))

        assertThat(updatedUser.organisation).isEqualTo(newOrganisation)
        assertThat(updatedUser.organisationId).isEqualTo(newOrganisation.id)
        assertThat(userRepository.findById(user.id)).isEqualTo(updatedUser)
    }

    @Test
    fun `updating user subjects`() {
        val maths = subjectService.addSubject(
            Subject(
                id = SubjectId(value = "1"),
                name = "Maths"
            )
        )
        val physics = subjectService.addSubject(
            Subject(
                id = SubjectId(value = "2"),
                name = "Physics"
            )
        )

        val user = userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(
                    id = "user-1"
                ),
                profile = ProfileFactory.sample(firstName = "Ada", lastName = "Lovelace", subjects = listOf(maths))
            )
        )

        userRepository.update(user, UserUpdate.ReplaceSubjects(listOf(maths, physics)))

        val updatedUser = userRepository.findById(user.id)!!

        assertThat(updatedUser.profile!!.subjects).containsExactly(maths, physics)
    }

    @Test
    fun `updating user ages`() {
        val user = userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(
                    id = "user-1"
                ),
                profile = ProfileFactory.sample(firstName = "Ada", lastName = "Lovelace", ages = listOf(9, 10, 11))
            )
        )

        userRepository.update(user, UserUpdate.ReplaceAges(listOf(6, 7, 8, 9)))

        val updatedUser = userRepository.findById(user.id)!!

        assertThat(updatedUser.profile!!.ages).containsExactly(6, 7, 8, 9)
    }

    @Test
    fun `updating marketing tracking fields`() {
        val user = userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(
                    id = "user-1"
                ),
                profile = ProfileFactory.sample(firstName = "Ada", lastName = "Lovelace")
            )
        )

        userRepository.update(
            user,
            UserUpdate.ReplaceMarketingTracking(
                utmCampaign = "test-campaign",
                utmSource = "test-source",
                utmContent = "test-content",
                utmMedium = "test-medium",
                utmTerm = "test-term"
            )
        )

        val updatedUser = userRepository.findById(user.id)!!

        assertThat(updatedUser.marketingTracking.utmCampaign).isEqualTo("test-campaign")
        assertThat(updatedUser.marketingTracking.utmContent).isEqualTo("test-content")
        assertThat(updatedUser.marketingTracking.utmMedium).isEqualTo("test-medium")
        assertThat(updatedUser.marketingTracking.utmSource).isEqualTo("test-source")
        assertThat(updatedUser.marketingTracking.utmTerm).isEqualTo("test-term")
    }

    @Test
    fun `updating user accessExpiresOn`() {
        val user = userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(
                    id = "user-1"
                ),
                accessExpiresOn = null
            )
        )
        val date = ZonedDateTime.now(ZoneOffset.UTC).plusWeeks(123)

        userRepository.update(user, UserUpdate.ReplaceAccessExpiresOn(date))

        val updatedUser = userRepository.findById(user.id)!!

        assertThat(updatedUser.accessExpiresOn).isEqualTo(date)
    }

    @Test
    fun `updating shareCode`() {
        val user = userRepository.create(
            UserFactory.sample(
                teacherPlatformAttributes = TeacherPlatformAttributesFactory.sample(shareCode = null)
            )
        )

        userRepository.update(user, UserUpdate.ReplaceShareCode("1234"))

        val updatedUser = userRepository.findById(user.id)!!

        assertThat(updatedUser.teacherPlatformAttributes!!.shareCode).isEqualTo("1234")
    }

    @Test
    fun `updating user hasLifetimeAccess`() {
        val user = userRepository.create(
            UserFactory.sample(
                teacherPlatformAttributes = TeacherPlatformAttributesFactory.sample(hasLifetimeAccess = false)
            )
        )
        userRepository.update(user, UserUpdate.ReplaceHasLifetimeAccess(true))

        val updatedUser = userRepository.findById(user.id)!!

        assertThat(updatedUser.teacherPlatformAttributes!!.hasLifetimeAccess).isEqualTo(true)
    }

    @Test
    fun `find users by organisation id`() {
        val organisationId1 = OrganisationId()
        val organisationId2 = OrganisationId()

        userRepository.create(
            UserFactory.sample(
                organisationId = organisationId1
            )
        )
        userRepository.create(
            UserFactory.sample(
                organisationId = organisationId2
            )
        )

        val usersInOrg = userRepository.findAllByOrganisationId(organisationId1)

        assertThat(usersInOrg).hasSize(1)
    }

    @Test
    fun `find users matching domain and not being part of organisation`() {
        val organisationId1 = OrganisationId()
        val organisationId2 = OrganisationId()

        userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(
                    id = "user-1",
                    username = "user1@me.com"
                ), organisationId = organisationId1
            )
        )
        userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(
                    id = "user-2",
                    username = "user1@me.com"
                ), organisationId = organisationId2
            )
        )
        userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(
                    id = "user-3",
                    username = "user1@meme.com"
                ), organisationId = organisationId1
            )
        )

        val matches = userRepository.findOrphans(domain = "me.com", organisationId = organisationId2)

        assertThat(matches).hasSize(1)
        assertThat(matches.first().id.value).isEqualTo("user-1")
        assertThat(matches.first().organisationId).isEqualTo(organisationId1)
    }
}
