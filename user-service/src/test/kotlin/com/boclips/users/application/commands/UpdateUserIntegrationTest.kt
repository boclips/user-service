package com.boclips.users.application.commands

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.application.UserUpdatesCommandFactory
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.marketing.CrmProfile
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.domain.service.MarketingService
import com.boclips.users.infrastructure.user.UserDocumentMongoRepository
import com.boclips.users.api.request.user.MarketingTrackingRequest
import com.boclips.users.api.request.user.UpdateUserRequest
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationDetailsFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.UpdateUserRequestFactory
import com.boclips.users.testsupport.factories.UserDocumentFactory
import com.boclips.users.testsupport.factories.UserFactory
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

class UpdateUserIntegrationTest : AbstractSpringIntegrationTest() {
    lateinit var updateUser: UpdateUser
    lateinit var mockMarketingService: MarketingService

    @Autowired
    lateinit var userDocumentMongoRepository: UserDocumentMongoRepository

    @BeforeEach
    fun setup() {
        mockMarketingService = mock()
        updateUser = UpdateUser(
            userService = userService,
            getOrImportUser = getOrImportUser,
            marketingService = mockMarketingService,
            organisationRepository = organisationRepository,
            organisationService = organisationService,
            userRepository = userRepository,
            userUpdatesCommandFactory = UserUpdatesCommandFactory(subjectService = subjectService),
            generateShareCode = GenerateTeacherShareCode()
        )
    }

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
    fun `when a user is updated, their CRM profile is too`() {
        subjectService.addSubject(Subject(name = "Maths", id = SubjectId(value = "subject-1")))

        val userId = UUID.randomUUID().toString()
        setSecurityContext(userId)

        saveUser(UserFactory.sample(identity = IdentityFactory.sample(id = userId, username = "josh@fleck.com")))
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

        argumentCaptor<List<CrmProfile>>().apply {
            verify(mockMarketingService).updateProfile(capture())

            assertThat(allValues).hasSize(1)
            val updatedProfiles = allValues.get(0)

            assertThat(updatedProfiles).hasSize(1)
            val updatedProfile = updatedProfiles.get(0)

            assertThat(updatedProfile.email).isEqualTo("josh@fleck.com")
            assertThat(updatedProfile.firstName).isEqualTo("josh")
            assertThat(updatedProfile.lastName).isEqualTo("fleck")
            assertThat(updatedProfile.activated).isTrue()
            assertThat(updatedProfile.hasOptedIntoMarketing).isTrue()
            assertThat(updatedProfile.ageRange).containsExactly(4, 5, 6)
            assertThat(updatedProfile.subjects).hasSize(1)
            assertThat(updatedProfile.subjects.first().name).isEqualTo("Maths")
            assertThat(updatedProfile.subjects.first().id).isEqualTo(SubjectId("subject-1"))
            assertThat(updatedProfile.marketingTracking.utmSource).isEqualTo("test-source")
            assertThat(updatedProfile.marketingTracking.utmMedium).isEqualTo("test-medium")
            assertThat(updatedProfile.marketingTracking.utmCampaign).isEqualTo("test-campaign")
            assertThat(updatedProfile.marketingTracking.utmTerm).isEqualTo("test-term")
            assertThat(updatedProfile.marketingTracking.utmContent).isEqualTo("test-content")
        }
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
                    identity = IdentityFactory.sample(id = userId),
                    profile = ProfileFactory.sample()
                )
            )

            val updatedUser =
                updateUser(userId, UpdateUserRequestFactory.sample(schoolName = "new school", country = "ESP"))

            val newSchool =
                organisationRepository.lookupSchools(schoolName = "new school", countryCode = "ESP")
                    .firstOrNull()
            assertThat(newSchool).isNotNull
            assertThat(updatedUser.organisationId?.value).isEqualTo(newSchool?.id)
        }

        @Test
        fun `existing school links school without creating duplicate`() {
            val userId = UUID.randomUUID().toString()
            setSecurityContext(userId)
            saveUser(
                UserFactory.sample(
                    identity = IdentityFactory.sample(id = userId),
                    profile = ProfileFactory.sample()
                )
            )
            val school =
                organisationRepository.save(
                    OrganisationFactory.school(
                        school = OrganisationDetailsFactory.school(
                            country = Country.fromCode(
                                "ESP"
                            )
                        )
                    )
                )

            val updatedUser = updateUser(
                userId,
                UpdateUserRequestFactory.sample(
                    schoolName = school.details.name,
                    country = "ESP"
                )
            )

            val newSchool =
                organisationRepository.lookupSchools(schoolName = school.details.name, countryCode = "ESP")
            assertThat(newSchool).hasSize(1)
            assertThat(updatedUser.organisationId?.value).isEqualTo(newSchool.first().id)
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
                    identity = IdentityFactory.sample(id = userId),
                    profile = ProfileFactory.sample()
                )
            )
            val school =
                organisationRepository.save(
                    OrganisationFactory.school(
                        school = OrganisationDetailsFactory.school(
                            country = Country.fromCode(
                                "USA"
                            )
                        )
                    )
                )

            val updatedUser = updateUser(
                userId,
                UpdateUserRequestFactory.sample(
                    country = "USA",
                    schoolId = school.details.externalId
                )
            )

            val newSchools =
                organisationRepository.lookupSchools(schoolName = school.details.name, countryCode = "USA")
            assertThat(newSchools).hasSize(1)
            assertThat(updatedUser.organisationId?.value).isEqualTo(newSchools.first().id)
        }

        @Test
        fun `updating to unlisted school-digger school preserves school address`() {
            val userId = UUID.randomUUID().toString()
            setSecurityContext(userId)
            val school =
                organisationRepository.save(
                    OrganisationFactory.school(
                        school = OrganisationDetailsFactory.school(
                            country = Country.fromCode("USA"),
                            state = State.fromCode("CA"),
                            externalId = "i'm in schooldigger"
                        )
                    )
                )

            saveUser(
                UserFactory.sample(
                    identity = IdentityFactory.sample(id = userId),
                    organisationId = school.id
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

            val organisationAccount =
                organisationRepository.findSchoolById(updatedUser.organisationId!!)!!
            assertThat(organisationAccount.details.country.isUSA()).isEqualTo(true)
            assertThat(organisationAccount.details.state!!.id).isEqualTo("AZ")
            assertThat(organisationAccount.details.externalId).isNull()
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

    @Test
    fun `update profile of external user`() {
        // this mimics a user not being in our local database,
        // but is instead provided by some SSO source
        val userId = UserId("some-user-id")
        keycloakClientFake.createAccount(IdentityFactory.sample(id = userId.value))
        setSecurityContext(userId.value)

        val mySubject = subjectService.addSubject(Subject(name = "Maths", id = SubjectId("subject-1")))

        val persistedUser = updateUser(
            userId.value, UpdateUserRequest(
                firstName = "josh",
                lastName = "fleck",
                hasOptedIntoMarketing = true,
                subjects = listOf(mySubject.id.value),
                ages = listOf(4, 5, 6)
            )
        )

        assertThat(persistedUser.profile!!.firstName).isEqualTo("josh")
        assertThat(persistedUser.profile!!.lastName).isEqualTo("fleck")
        assertThat(persistedUser.profile!!.subjects).containsExactly(mySubject)
        assertThat(persistedUser.profile!!.ages).isEqualTo(listOf(4, 5, 6))
        assertThat(persistedUser.profile!!.hasOptedIntoMarketing).isEqualTo(true)
    }

    @Nested
    @DisplayName("Access expiry")
    inner class accessExpiry {

        // This part added because we provide extended trial during the COVID-19 situation, can be removed after 2020-06-30
        @Test
        fun `accessExpiresOn logic starts after 30th June 2020`() {
            setSecurityContext("new-user-id")
            val defaultExpectedExpiryDate = ZonedDateTime.now().plusDays(
                UpdateUser.DEFAULT_TRIAL_DAYS_LENGTH + 1
            ).truncatedTo(ChronoUnit.DAYS)
            val extendedTrialEndDate = Instant.parse("2020-06-30T00:00:00Z").atZone(ZoneId.systemDefault())

            val expectedExpiryDate = if (defaultExpectedExpiryDate.isBefore(extendedTrialEndDate)) {
                extendedTrialEndDate
            } else {
                defaultExpectedExpiryDate
            }


            val newUserDocument =
                UserDocumentFactory.sample(
                    id = "new-user-id",
                    firstName = null,
                    createdAt = Instant.parse("2020-10-10T00:00:00Z"),
                    organisationId = null
                )

            userDocumentMongoRepository.save(newUserDocument)

            val updatedUser = updateUser("new-user-id", UpdateUserRequestFactory.sample(firstName = "Joesph"))

            assertThat(updatedUser.accessExpiresOn).isNotNull()

            val accessExpiresOn = updatedUser.accessExpiresOn

            assertThat(accessExpiresOn).isEqualTo(expectedExpiryDate)
        }

        @Test
        fun `it does not set accessExpiresOn for a lifetime user`() {
            setSecurityContext("lifetime-user-id")

            val lifetimeUserDocument =
                UserDocumentFactory.sample(
                    id = "lifetime-user-id",
                    firstName = "Joe",
                    organisationId = null,
                    hasLifetimeAccess = true
                )

            userDocumentMongoRepository.save(lifetimeUserDocument)

            val updatedUser = updateUser("lifetime-user-id", UpdateUserRequestFactory.sample(firstName = "Joesph"))

            assertThat(updatedUser.accessExpiresOn).isNull()
        }
    }
}