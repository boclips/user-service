package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.service.UserUpdateCommand
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.MarketingTrackingFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.Locale

class MongoUserRepositoryTest : AbstractSpringIntegrationTest() {

    @Test
    fun `persists account`() {
        val account = AccountFactory.sample(roles = listOf("ROLE_TEACHER"))
        userRepository.save(account)

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

        userRepository.save(user)

        val fetchedUser = userRepository.findById(user.id)

        assertThat(fetchedUser).isEqualTo(user)
    }

    @Test
    fun `can get all accounts`() {
        val savedUsers = listOf(
            userRepository.save(UserFactory.sample()),
            userRepository.save(UserFactory.sample())
        )

        assertThat(userRepository.findAll(savedUsers.map { it.id })).containsAll(savedUsers)
    }

    @Test
    fun `updating user first name field only replaces first name`() {
        val user = userRepository.save(
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
        val user = userRepository.save(
            UserFactory.sample(
                account = AccountFactory.sample(
                    id = "user-1"
                ),
                profile = ProfileFactory.sample(
                    firstName = "Ada",
                    lastName = "Lovelace",
                    hasOptedIntoMarketing = false,
                    country = Country(id = Locale.CANADA.isO3Country, name = Locale.CANADA.displayCountry)
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
            UserUpdateCommand.ReplaceCountry(Country.fromCode("USA")),
            UserUpdateCommand.ReplaceOrganisationId(OrganisationAccountId("my-id"))
        )

        val updatedUser = userRepository.findById(user.id)!!

        assertThat(updatedUser.profile!!.lastName).isEqualTo("Earhart")
        assertThat(updatedUser.profile!!.hasOptedIntoMarketing).isEqualTo(true)
        assertThat(updatedUser.profile!!.country!!.id).isEqualTo("USA")
        assertThat(updatedUser.profile!!.country!!.name).isEqualTo("United States")
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

        val user = userRepository.save(
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
        val user = userRepository.save(
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
        val user = userRepository.save(
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
}
