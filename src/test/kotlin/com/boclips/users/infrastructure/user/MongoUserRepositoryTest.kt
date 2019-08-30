package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.OrganisationType
import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.service.UserUpdateCommand
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.MarketingTrackingFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UserFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MongoUserRepositoryTest : AbstractSpringIntegrationTest() {

    @Test
    fun `persists account`() {
        val account = AccountFactory.sample()
        userRepository.save(account)

        val fetchedUser = userRepository.findById(account.id)!!

        assertThat(fetchedUser.id).isNotNull()
        assertThat(fetchedUser.account).isEqualTo(account)
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
                    id = "user-1",
                    organisationType = OrganisationType.BoclipsForTeachers
                ),
                profile = ProfileFactory.sample(
                    firstName = "Ada",
                    lastName = "Lovelace",
                    hasOptedIntoMarketing = false,
                    country = "United Kingdom",
                    state = "New York",
                    school = "Brooklyn School"
                ),
                referralCode = ""

            )
        )

        val organisationType = OrganisationType.District(organisationId = OrganisationId("district-id"))

        userRepository.update(
            user,
            UserUpdateCommand.ReplaceLastName("Earhart"),
            UserUpdateCommand.ReplaceHasOptedIntoMarketing(true),
            UserUpdateCommand.ReplaceReferralCode("1234"),
            UserUpdateCommand.ReplaceCountry("United States of America"),
            UserUpdateCommand.ReplaceState("California"),
            UserUpdateCommand.ReplaceSchool("Sunnydale High School"),
            UserUpdateCommand.ReplaceOrganisation(organisationType)
        )

        val updatedUser = userRepository.findById(user.id)!!

        assertThat(updatedUser.profile!!.lastName).isEqualTo("Earhart")
        assertThat(updatedUser.profile!!.hasOptedIntoMarketing).isEqualTo(true)
        assertThat(updatedUser.profile!!.country).isEqualTo("United States of America")
        assertThat(updatedUser.profile!!.state).isEqualTo("California")
        assertThat(updatedUser.profile!!.school).isEqualTo("Sunnydale High School")
        assertThat(updatedUser.referralCode).isEqualTo("1234")
        assertThat(updatedUser.account.organisationType).isEqualTo(organisationType)
    }

    @Test
    fun `updating user subjects`() {
        val maths = Subject(
            id = SubjectId(value = "1"),
            name = "Maths"
        )
        val physics = Subject(
            id = SubjectId(value = "2"),
            name = "Physics"
        )

        whenever(subjectService.getSubjectsById(any())).thenReturn(
            listOf(maths, physics)
        )

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

    @Nested
    inner class OrganisationAssociation {
        @Test
        fun `there exist teachers which are not associated to an organisation`() {
            val teacher = UserFactory.sample(account = AccountFactory.sample(organisationType = OrganisationType.BoclipsForTeachers))

            val savedTeacher = userRepository.save(teacher)

            assertThat(teacher).isEqualTo(savedTeacher)
        }

        @Test
        fun `there exist users are associated to API clients`() {
            val user = UserFactory.sample(
                account = AccountFactory.sample(
                    organisationType = OrganisationType.ApiCustomer(
                        OrganisationId("some-org-id")
                    )
                )
            )

            val savedUser = userRepository.save(user)

            assertThat(user).isEqualTo(savedUser)
        }

        @Test
        fun `there exist users which are associated to school districts`() {
            val teacher = UserFactory.sample(
                account = AccountFactory.sample(
                    organisationType = OrganisationType.District(
                        OrganisationId("some-district-id")
                    )
                )
            )

            val savedTeacher = userRepository.save(teacher)

            assertThat(teacher).isEqualTo(savedTeacher)
        }
    }
}
