package com.boclips.users.application.commands

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.domain.service.OrganisationService
import com.boclips.users.presentation.requests.MarketingTrackingRequest
import com.boclips.users.presentation.requests.UpdateUserRequest
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.AccountFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UpdateUserRequestFactory
import com.boclips.users.testsupport.factories.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class UpdateUserIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var updateUser: UpdateUser

    @Test
    fun `update user information`() {
        subjectService.addSubject(Subject(name = "Maths", id = SubjectId(value = "subject-1")))

        val userId = UUID.randomUUID().toString()
        setSecurityContext(userId)

        saveUser(UserFactory.sample(id = userId))
        updateUser(
            userId, UpdateUserRequest(
                firstName = "josh",
                lastName = "fleck",
                hasOptedIntoMarketing = true,
                subjects = listOf("subject-1"),
                ages = listOf(4, 5, 6),
                country = "USA",
                referralCode = "1234",
                utm = MarketingTrackingRequest(
                    source = "test-source",
                    medium = "test-medium",
                    campaign = "test-campaign",
                    term = "test-term",
                    content = "test-content"
                )
            )
        )

        val user = userRepository.findById(UserId(userId))!!

        val profile = user.profile!!
        assertThat(profile.firstName).isEqualTo("josh")
        assertThat(profile.lastName).isEqualTo("fleck")
        assertThat(profile.hasOptedIntoMarketing).isTrue()
        assertThat(profile.ages).containsExactly(4, 5, 6)
        assertThat(profile.subjects).hasSize(1)
        assertThat(profile.subjects.first().name).isEqualTo("Maths")
        assertThat(profile.subjects.first().id).isEqualTo(SubjectId("subject-1"))
        assertThat(user.referralCode).isEqualTo("1234")
        assertThat(user.marketingTracking.utmSource).isEqualTo("test-source")
        assertThat(user.marketingTracking.utmMedium).isEqualTo("test-medium")
        assertThat(user.marketingTracking.utmCampaign).isEqualTo("test-campaign")
        assertThat(user.marketingTracking.utmTerm).isEqualTo("test-term")
        assertThat(user.marketingTracking.utmContent).isEqualTo("test-content")
    }

    @Test
    fun `update user is idempotent`() {
        val userId = UUID.randomUUID().toString()
        setSecurityContext(userId)

        saveUser(UserFactory.sample(id = userId))

        val updateUserRequest = UpdateUserRequestFactory.sample()
        assertThat(updateUser(userId, updateUserRequest)).isEqualTo(updateUser(userId, updateUserRequest))
    }

    @Nested
    @DisplayName("When non USA")
    inner class NonUsaNewSchool {

        @Test
        fun `unexisting school creates new independent school`() {
            val userId = UUID.randomUUID().toString()
            setSecurityContext(userId)
            saveUser(
                UserFactory.sample(
                    account = AccountFactory.sample(id = userId),
                    profile = ProfileFactory.sample()
                )
            )

            val updatedUser =
                updateUser(userId, UpdateUserRequestFactory.sample(schoolName = "new school", country = "ESP"))

            val newSchool =
                organisationAccountRepository.lookupSchools(schoolName = "new school", countryCode = "ESP")
                    .firstOrNull()
            assertThat(newSchool).isNotNull
            assertThat(updatedUser.organisationAccountId?.value).isEqualTo(newSchool?.id)
        }

        @Test
        fun `existing school links school without creating duplicate`() {
            val userId = UUID.randomUUID().toString()
            setSecurityContext(userId)
            saveUser(
                UserFactory.sample(
                    account = AccountFactory.sample(id = userId),
                    profile = ProfileFactory.sample()
                )
            )
            val school =
                organisationAccountRepository.save(OrganisationFactory.school(country = Country.fromCode("ESP")))

            val updatedUser = updateUser(
                userId,
                UpdateUserRequestFactory.sample(
                    schoolName = school.organisation.name,
                    country = "ESP"
                )
            )

            val newSchool =
                organisationAccountRepository.lookupSchools(schoolName = school.organisation.name, countryCode = "ESP")
            assertThat(newSchool).hasSize(1)
            assertThat(updatedUser.organisationAccountId?.value).isEqualTo(newSchool.first().id)
        }
    }

    @Nested
    @DisplayName("When USA")
    inner class UsaNewSchool {
        @Test
        fun `identified school links school without creating duplicate`() {
            val userId = UUID.randomUUID().toString()
            setSecurityContext(userId)
            saveUser(
                UserFactory.sample(
                    account = AccountFactory.sample(id = userId),
                    profile = ProfileFactory.sample()
                )
            )
            val school =
                organisationAccountRepository.save(OrganisationFactory.school(country = Country.fromCode("USA")))

            val updatedUser = updateUser(
                userId,
                UpdateUserRequestFactory.sample(
                    country = "USA",
                    schoolId = school.organisation.externalId
                )
            )

            val newSchool =
                organisationAccountRepository.lookupSchools(schoolName = school.organisation.name, countryCode = "USA")
            assertThat(newSchool).hasSize(1)
            assertThat(updatedUser.organisationAccountId?.value).isEqualTo(newSchool.first().id)
        }

        @Test
        fun `updating to unlisted school-digger school preserves school address`() {
            val userId = UUID.randomUUID().toString()
            setSecurityContext(userId)
            val school =
                organisationAccountRepository.save(OrganisationFactory.school(country = Country.fromCode("USA"), state = State.fromCode("CA"), externalId = "i'm in schooldigger"))

            saveUser(
                UserFactory.sample(
                    account = AccountFactory.sample(id = userId),
                    organisationAccountId= school.id
                )
            )
            val updatedUser = updateUser(
                userId,
                UpdateUserRequestFactory.sample(
                    schoolId = "",
                    schoolName = "",
                    state = "AZ",
                    country = "USA"
                )
            )

            val organisationAccount = organisationAccountRepository.findSchoolById(updatedUser.organisationAccountId!!)!!
            assertThat(organisationAccount.organisation.country.isUSA()).isEqualTo(true)
            assertThat(organisationAccount.organisation.state!!.id).isEqualTo("AZ")
            assertThat(organisationAccount.organisation.externalId).isNull()
        }
    }

    @Test
    fun `cannot update user when security context not populated`() {
        assertThrows<NotAuthenticatedException> {
            updateUser("peanuts", UpdateUserRequestFactory.sample())
        }
    }

    @Test
    fun `cannot obtain user information of another user`() {
        val userId = UUID.randomUUID().toString()
        setSecurityContext("different-user")

        saveUser(UserFactory.sample())

        assertThrows<PermissionDeniedException> { updateUser(userId, UpdateUserRequestFactory.sample()) }
    }

    @Test
    fun `get user throws when user doesn't exist anywhere`() {
        val userId = UUID.randomUUID().toString()
        setSecurityContext(userId)

        assertThrows<UserNotFoundException> { updateUser(userId, UpdateUserRequestFactory.sample()) }
    }
}
