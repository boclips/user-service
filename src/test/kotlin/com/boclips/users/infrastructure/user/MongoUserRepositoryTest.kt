package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.account.OrganisationAccountId
import com.boclips.users.domain.service.UserUpdateCommand
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.MarketingTrackingFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.TeacherPlatformAttributesFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZoneOffset
import java.time.ZonedDateTime

class MongoUserRepositoryTest : AbstractSpringIntegrationTest() {

    @Test
    fun `persists account`() {
        val account = AccountFactory.sample(roles = listOf("ROLE_TEACHER"))
        userRepository.create(account)

        val fetchedUser = userRepository.findById(account.id)!!

        assertThat(fetchedUser.id).isNotNull()
        assertThat(fetchedUser.account.email).isEqualTo(account.email)
        assertThat(fetchedUser.account.id).isEqualTo(account.id)
    }

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
                account = AccountFactory.sample(
                    id = "user-1"
                ),
                profile = ProfileFactory.sample(firstName = "Ada", lastName = "Lovelace")
            )
        )

        userRepository.update(user, UserUpdateCommand.ReplaceFirstName("Amelia"))

        val updatedUser = userRepository.findById(user.id)!!

        assertThat(updatedUser.profile!!.firstName).isEqualTo("Amelia")
        assertThat(updatedUser.profile!!.lastName).isEqualTo("Lovelace")
    }

    @Test
    fun `updating multiple fields`() {
        val user = userRepository.create(
            UserFactory.sample(
                account = AccountFactory.sample(
                    id = "user-1"
                ),
                profile = ProfileFactory.sample(
                    firstName = "Ada",
                    lastName = "Lovelace",
                    hasOptedIntoMarketing = false
                ),
                referralCode = "",
                organisationAccountId = null
            )
        )

        userRepository.update(
            user,
            UserUpdateCommand.ReplaceLastName("Earhart"),
            UserUpdateCommand.ReplaceHasOptedIntoMarketing(true),
            UserUpdateCommand.ReplaceReferralCode("1234"),
            UserUpdateCommand.ReplaceOrganisationId(OrganisationAccountId("my-id"))
        )

        val updatedUser = userRepository.findById(user.id)!!

        assertThat(updatedUser.profile!!.lastName).isEqualTo("Earhart")
        assertThat(updatedUser.profile!!.hasOptedIntoMarketing).isEqualTo(true)
        assertThat(updatedUser.referralCode).isEqualTo("1234")
        assertThat(updatedUser.organisationAccountId).isEqualTo(OrganisationAccountId("my-id"))
    }

    @Test
    fun `updating user subjects`() {
        val maths = subjectService.addSubject(Subject(
            id = SubjectId(value = "1"),
            name = "Maths"
        ))
        val physics = subjectService.addSubject(Subject(
            id = SubjectId(value = "2"),
            name = "Physics"
        ))

        val user = userRepository.create(
            UserFactory.sample(
                account = AccountFactory.sample(
                    id = "user-1"
                ),
                profile = ProfileFactory.sample(firstName = "Ada", lastName = "Lovelace", subjects = listOf(maths))
            )
        )

        userRepository.update(user, UserUpdateCommand.ReplaceSubjects(listOf(maths, physics)))

        val updatedUser = userRepository.findById(user.id)!!

        assertThat(updatedUser.profile!!.subjects).containsExactly(maths, physics)
    }

    @Test
    fun `updating user ages`() {
        val user = userRepository.create(
            UserFactory.sample(
                account = AccountFactory.sample(
                    id = "user-1"
                ),
                profile = ProfileFactory.sample(firstName = "Ada", lastName = "Lovelace", ages = listOf(9, 10, 11))
            )
        )

        userRepository.update(user, UserUpdateCommand.ReplaceAges(listOf(6, 7, 8, 9)))

        val updatedUser = userRepository.findById(user.id)!!

        assertThat(updatedUser.profile!!.ages).containsExactly(6, 7, 8, 9)
    }

    @Test
    fun `updating marketing tracking fields`() {
        val user = userRepository.create(
            UserFactory.sample(
                account = AccountFactory.sample(
                    id = "user-1"
                ),
                profile = ProfileFactory.sample(firstName = "Ada", lastName = "Lovelace")
            )
        )

        userRepository.update(
            user,
            UserUpdateCommand.ReplaceMarketingTracking(
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
                account = AccountFactory.sample(
                    id = "user-1"
                ),
                accessExpiresOn = null
            )
        )
        val date = ZonedDateTime.now(ZoneOffset.UTC).plusWeeks(123)

        userRepository.update(user, UserUpdateCommand.ReplaceAccessExpiresOn(date))

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

        userRepository.update(user, UserUpdateCommand.ReplaceShareCode("1234"))

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
        userRepository.update(user, UserUpdateCommand.ReplaceHasLifetimeAccess(true))

        val updatedUser = userRepository.findById(user.id)!!

        assertThat(updatedUser.teacherPlatformAttributes!!.hasLifetimeAccess).isEqualTo(true)
    }

    @Test
    fun `find users by organisation id`() {
        userRepository.create(UserFactory.sample(account = AccountFactory.sample(id = "user-1"), organisationAccountId = OrganisationAccountId("org-id-1")))
        userRepository.create(UserFactory.sample(account = AccountFactory.sample(id = "user-2"), organisationAccountId = OrganisationAccountId("org-id-2")))

        val usersInOrg = userRepository.findAllByOrganisationId(OrganisationAccountId("org-id-1"))

        assertThat(usersInOrg).hasSize(1)
    }
}
