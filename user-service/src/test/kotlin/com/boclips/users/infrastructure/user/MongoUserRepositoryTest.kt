package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.user.UserUpdate
import com.boclips.users.infrastructure.keycloak.UserAlreadyExistsException
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.MarketingTrackingFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.TeacherPlatformAttributesFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZoneOffset
import java.time.ZonedDateTime

class MongoUserRepositoryTest : AbstractSpringIntegrationTest() {

    @Test
    fun `creating a user`() {
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
    fun `exception is thrown when trying to create a user which already exist`() {
        val user = UserFactory.sample(id = "user-id")
        userRepository.create(user)

        assertThrows<UserAlreadyExistsException> {
            userRepository.create(user)
        }
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
    fun `can find all teachers`() {
        val school = organisationRepository.save(OrganisationFactory.school())
        val district = organisationRepository.save(OrganisationFactory.district())
        val apiOrganisation = saveOrganisation(OrganisationFactory.apiIntegration())

        listOf(
            saveUser(
                UserFactory.sample(
                    profile = ProfileFactory.sample(firstName = "school-teacher"),
                    organisation = school
                )
            ),
            saveUser(
                UserFactory.sample(
                    profile = ProfileFactory.sample(firstName = "district-teacher"),
                    organisation = district
                )
            ),
            saveUser(
                UserFactory.sample(
                    profile = ProfileFactory.sample(firstName = "null-teacher"),
                    organisation = null
                )
            ),
            saveUser(
                UserFactory.sample(
                    profile = ProfileFactory.sample(firstName = "api-user"),
                    organisation = apiOrganisation
                )
            )
        )

        val users = userRepository.findAllTeachers()

        assertThat(users.map { it.getContactDetails()!!.firstName })
            .containsExactly("school-teacher", "district-teacher", "null-teacher")
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
                organisation = null
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
            district = OrganisationFactory.district()
        )

        val updatedUser = userRepository.update(user, UserUpdate.ReplaceOrganisation(newOrganisation))

        assertThat(updatedUser.organisation).usingRecursiveComparison().isEqualTo(newOrganisation)

        assertThat(userRepository.findById(user.id)).isEqualTo(updatedUser)
    }

    @Test
    fun `updating profile school`() {
        val school = organisationRepository.save(OrganisationFactory.school())
        val user = userRepository.create(UserFactory.sample(profile = null))

        val updatedUser = userRepository.update(user, UserUpdate.ReplaceProfileSchool(school))

        assertThat(updatedUser.profile?.school).isEqualTo(school)
        assertThat(userRepository.findById(user.id)).isEqualTo(updatedUser)
    }

    @Test
    fun `updating user subjects`() {
        val maths = saveSubject("Maths")
        val physics = saveSubject("Physics")

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

        assertThat(updatedUser.accessExpiresOn).isEqualToIgnoringNanos(date)
    }

    @Test
    fun `updating shareCode`() {
        val user = userRepository.create(
            UserFactory.sample(
                shareCode = null
            )
        )

        userRepository.update(user, UserUpdate.ReplaceShareCode("1234"))

        val updatedUser = userRepository.findById(user.id)!!

        assertThat(updatedUser.shareCode).isEqualTo("1234")
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
        val organisation1 = OrganisationFactory.school()
        val organisation2 = OrganisationFactory.school()

        userRepository.create(
            UserFactory.sample(
                organisation = organisation1
            )
        )
        userRepository.create(
            UserFactory.sample(
                organisation = organisation2
            )
        )

        val usersInOrg = userRepository.findAllByOrganisationId(organisation1.id)

        assertThat(usersInOrg).hasSize(1)
    }

    @Test
    fun `find users matching domain and not being part of organisation`() {
        val organisation1 = OrganisationFactory.school()
        val organisation2 = OrganisationFactory.school()

        userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(
                    id = "user-1",
                    username = "user1@me.com"
                ), organisation = organisation1
            )
        )
        userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(
                    id = "user-2",
                    username = "user1@me.com"
                ), organisation = organisation2
            )
        )
        userRepository.create(
            UserFactory.sample(
                identity = IdentityFactory.sample(
                    id = "user-3",
                    username = "user1@meme.com"
                ),
                organisation = organisation1
            )
        )

        val matches = userRepository.findOrphans(domain = "me.com", organisationId = organisation2.id)

        assertThat(matches).hasSize(1)
        assertThat(matches.first().id.value).isEqualTo("user-1")
        assertThat(matches.first().organisation?.id).isEqualTo(organisation1.id)
    }
}
