package com.boclips.users.presentation.controllers

import com.boclips.security.testing.setSecurityContext
import com.boclips.users.config.security.UserRoles
import com.boclips.users.domain.model.user.Identity
import com.boclips.users.domain.model.user.User
import com.boclips.users.domain.model.user.UserId
import com.boclips.users.domain.model.analytics.AnalyticsId
import com.boclips.users.domain.model.access.AccessRule
import com.boclips.users.domain.model.access.AccessRuleId
import com.boclips.users.domain.model.access.CollectionId
import com.boclips.users.domain.model.access.ContentPackage
import com.boclips.users.domain.model.access.ContentPackageId
import com.boclips.users.domain.model.access.VideoId
import com.boclips.users.domain.model.organisation.Address
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import com.boclips.users.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.testsupport.asApiUser
import com.boclips.users.testsupport.asBackofficeUser
import com.boclips.users.testsupport.asBoclipsService
import com.boclips.users.testsupport.asTeacher
import com.boclips.users.testsupport.asUser
import com.boclips.users.testsupport.asUserWithRoles
import com.boclips.users.testsupport.factories.ContentPackageFactory
import com.boclips.users.testsupport.factories.IdentityFactory
import com.boclips.users.testsupport.factories.OrganisationFactory
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.deal
import com.boclips.users.testsupport.factories.OrganisationFactory.Companion.school
import com.boclips.users.testsupport.factories.ProfileFactory
import com.boclips.users.testsupport.factories.TeacherPlatformAttributesFactory
import com.boclips.users.testsupport.factories.UserFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.ZonedDateTime

class UserControllerIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun `can create a new user with valid request`() {
        mvc.perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                     "email": "jane@doe.com",
                     "password": "Champagn3",
                     "analyticsId": "mxp-123",
                     "referralCode": "RR-123",
                     "recaptchaToken": "captcha-123"
                     }
                    """.trimIndent()
                )
        )
            .andExpect(status().isCreated)
            .andExpect(header().exists("Location"))
    }

    @Test
    fun `can create a new user without optional fields`() {
        mvc.perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                     "email": "jane@doe.com",
                     "password": "Champagn3",
                     "recaptchaToken": "captcha-123"
                     }
                    """.trimIndent()
                )
        )
            .andExpect(status().isCreated)
            .andExpect(header().exists("Location"))
    }

    @Test
    fun `can handle conflicts with valid request`() {
        val payload = """
                    {
                     "email": "jane@doe.com",
                     "password": "Champagn3",
                     "analyticsId": "mxp-123",
                     "referralCode": "RR-123",
                     "recaptchaToken": "captcha-123"
                     }
                    """.trimIndent()

        mvc.perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        )
            .andExpect(status().isCreated)

        mvc.perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        )
            .andExpect(status().isConflict)
    }

    @Test
    fun `cannot create account with invalid request`() {
        mvc.perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                     "email": "jane@doe.com"
                     }
                    """.trimIndent()
                )
        )
            .andExpect(status().isBadRequest)
            .andExpectApiErrorPayload()
    }

    @Test
    fun `cannot create account as a robot`() {
        whenever(captchaProvider.validateCaptchaToken(any())).thenReturn(false)

        mvc.perform(
            post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                     "email": "jane@doe.com",
                     "password": "Champagn3",
                     "analyticsId": "mxp-123",
                     "referralCode": "RR-123",
                     "recaptchaToken": "captcha-123"
                     }
                    """.trimIndent()
                )
        )
            .andExpect(status().isBadRequest)
    }

    @Nested
    inner class UpdateUser {
        @Test
        fun `updates a user`() {
            val subject = saveSubject("Maths")
            val user = saveUser(UserFactory.sample())
            val school = saveOrganisation(
                OrganisationFactory.school(
                    name = "San Fran Forest School",
                    address = Address(
                        state = State.fromCode("CA"),
                        country = Country.fromCode("USA")
                    )
                )
            )

            mvc.perform(
                put("/v1/users/${user.id.value}").asTeacher(user.id.value)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {"firstName": "jane",
                         "lastName": "doe",
                         "subjects": ["${subject.id.value}"],
                         "ages": [4,5,6],
                         "country": "USA",
                         "state": "CA",
                         "schoolName": "San Fran Forest School"
                         }
                        """.trimIndent()
                    )
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._links.profile.href", endsWith("/users/${user.id.value}")))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName", equalTo("jane")))
                .andExpect(jsonPath("$.lastName", equalTo("doe")))
                .andExpect(jsonPath("$.ages", equalTo(listOf(4, 5, 6))))
                .andExpect(jsonPath("$.subjects", hasSize<Int>(1)))
                .andExpect(jsonPath("$.school.name", equalTo("San Fran Forest School")))
                .andExpect(jsonPath("$.school.state.name", equalTo("California")))
                .andExpect(jsonPath("$.school.state.id", equalTo("CA")))
                .andExpect(jsonPath("$.school.country.name", equalTo("United States")))
                .andExpect(jsonPath("$.school.country.id", equalTo("USA")))
                .andExpect(jsonPath("$.organisation.id", equalTo(school.id.value)))
                .andExpect(jsonPath("$.organisation.name", equalTo("San Fran Forest School")))
                .andExpect(jsonPath("$.organisation.state.name", equalTo("California")))
                .andExpect(jsonPath("$.organisation.state.id", equalTo("CA")))
                .andExpect(jsonPath("$.organisation.country.name", equalTo("United States")))
                .andExpect(jsonPath("$.organisation.country.id", equalTo("USA")))
        }

        @Test
        fun `returns a 403 response if caller tries to update a different user and not have ROLE_UPDATE_USERS`() {
            saveUser(UserFactory.sample(identity = IdentityFactory.sample(id = "user-id")))

            mvc.perform(
                put("/v1/users/user-id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                             "firstName": "change this",
                             "lastName": "and that"
                        }
                        """.trimIndent()
                    ).asUser("different-users-id")
            )
                .andExpect(status().isForbidden)
        }

        @Test
        fun `can update user if caller is different user but has ROLE_UPDATE_USERS`() {
            saveUser(
                UserFactory.sample(
                    identity = IdentityFactory.sample(id = "user-id"),
                    profile = ProfileFactory.sample(firstName = "oldname")
                )
            )


            mvc.perform(
                put("/v1/users/user-id").asUserWithRoles(
                    "different-user-id",
                    UserRoles.UPDATE_USERS,
                    UserRoles.VIEW_USERS
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {"firstName": "newname"
                         }
                        """.trimIndent()
                    )
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._links.profile.href", endsWith("/users/user-id")))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName", equalTo("newname")))
        }

        @Test
        fun `updating without a payload is a bad request`() {
            saveUser(UserFactory.sample())

            setSecurityContext("user-id")

            mvc.perform(put("/v1/users/user-id").asUser("user-id"))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `invalid state`() {
            saveSubject("Maths")
            saveUser(UserFactory.sample())
            saveOrganisation(
                OrganisationFactory.school(
                    name = "San Fran Forest School",
                    address = Address(
                        state = State.fromCode("CA"),
                        country = Country.fromCode("USA")
                    )
                )
            )

            setSecurityContext("user-id")

            mvc.perform(
                put("/v1/users/user-id").asUser("user-id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {"firstName": "jane",
                         "lastName": "doe",
                         "subjects": ["subject-1"],
                         "ages": [4,5,6],
                         "country": "USA",
                         "state": "XX",
                         "schoolName": "A new school name"
                         }
                        """.trimIndent()
                    )
            )
                .andExpect(status().isBadRequest)
                .andExpectApiErrorPayload()
        }

        @Test
        fun `successful onboarding sets up the trial access date`() {
            val subject = saveSubject("Maths")
            val user = setupSampleUserBeforeOnboarding("user-id")

            val userBeforeOnboarding = userRepository.findById(user.id)
            assertThat(userBeforeOnboarding!!.accessExpiresOn).isNull()

            mvc.perform(
                put("/v1/users/user-id").asUser("user-id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {"firstName": "jane",
                         "lastName": "doe",
                         "subjects": ["${subject.id.value}"],
                         "ages": [4,5,6],
                         "country": "USA",
                         "state": "CA",
                         "schoolName": "San Fran Forest School"
                         }
                        """.trimIndent()
                    )
            )
                .andExpect(status().isOk)

            val userAfterOnboarding = userRepository.findById(user.id)
            assertThat(userAfterOnboarding!!.accessExpiresOn).isNotNull()
        }

        @Test
        fun `successful onboarding sets up shareCode if user do not have it - sso users`() {
            val user = UserFactory.sample(
                analyticsId = AnalyticsId(
                    value = "1234567"
                ),
                teacherPlatformAttributes = TeacherPlatformAttributesFactory.sample(shareCode = null),
                identity = IdentityFactory.sample(id = "user-id"),
                profile = null,
                organisation = null,
                accessExpiresOn = null
            )

            saveUser(user)

            val subject = saveSubject("Maths")
            saveOrganisation(
                OrganisationFactory.school(
                    name = "San Fran Forest School",
                    address = Address(
                        state = State.fromCode("CA"),
                        country = Country.fromCode("USA")
                    )
                )
            )

            setSecurityContext("user-id")

            val userBeforeOnboarding = userRepository.findById(user.id)
            assertThat(userBeforeOnboarding!!.teacherPlatformAttributes!!.shareCode).isNull()

            mvc.perform(
                put("/v1/users/user-id").asUser("user-id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {"firstName": "jane",
                         "lastName": "doe",
                         "subjects": ["${subject.id.value}"],
                         "ages": [4,5,6],
                         "country": "USA",
                         "state": "CA",
                         "schoolName": "San Fran Forest School"
                         }
                        """.trimIndent()
                    )
            )
                .andExpect(status().isOk)

            val userAfterOnboarding = userRepository.findById(user.id)
            assertThat(userAfterOnboarding!!.teacherPlatformAttributes!!.shareCode).isNotNull()
        }

        @Test
        fun `successful onboarding does not override shareCode if user already has one`() {
            val user = UserFactory.sample(
                analyticsId = AnalyticsId(
                    value = "1234567"
                ),
                teacherPlatformAttributes = TeacherPlatformAttributesFactory.sample(shareCode = "HYML"),
                identity = IdentityFactory.sample(id = "user-id"),
                profile = null,
                organisation = null,
                accessExpiresOn = null
            )

            saveUser(user)

            val maths = saveSubject("Maths")
            saveOrganisation(
                OrganisationFactory.school(
                    name = "San Fran Forest School",
                    address = Address(
                        state = State.fromCode("CA"),
                        country = Country.fromCode("USA")
                    )
                )
            )

            setSecurityContext("user-id")

            mvc.perform(
                put("/v1/users/user-id").asUser("user-id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {"firstName": "jane",
                         "lastName": "doe",
                         "subjects": ["${maths.id.value}"],
                         "ages": [4,5,6],
                         "country": "USA",
                         "state": "CA",
                         "schoolName": "San Fran Forest School"
                         }
                        """.trimIndent()
                    )
            )
                .andExpect(status().isOk)

            val userAfterOnboarding = userRepository.findById(user.id)
            assertThat(userAfterOnboarding!!.teacherPlatformAttributes!!.shareCode).isEqualTo("HYML")
        }

        @Test
        fun `updates for an onboarded user does not change the access expiry`() {
            val subject = saveSubject("Maths")
            val user = setupSampleUserBeforeOnboarding("user-id")

            val userBeforeOnboarding = userRepository.findById(user.id)
            assertThat(userBeforeOnboarding!!.accessExpiresOn).isNull()

            mvc.perform(
                put("/v1/users/user-id").asUser("user-id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {"firstName": "jane",
                         "lastName": "doe",
                         "subjects": ["${subject.id.value}"],
                         "ages": [4,5,6],
                         "country": "USA",
                         "state": "CA",
                         "schoolName": "San Fran Forest School"
                         }
                        """.trimIndent()
                    )
            )
                .andExpect(status().isOk)

            val userAfterOnboarding = userRepository.findById(user.id)

            val originalAccessExpiresOn = userAfterOnboarding!!.accessExpiresOn

            mvc.perform(
                put("/v1/users/user-id").asUser("user-id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {"firstName": "Joseph",
                         "lastName": "Smith"
                         }
                        """.trimIndent()
                    )
            )
                .andExpect(status().isOk)

            val userAfterUpdate = userRepository.findById(user.id)

            assertThat(userAfterUpdate!!.profile!!.firstName).isEqualTo("Joseph")
            assertThat(userAfterUpdate.profile!!.lastName).isEqualTo("Smith")
            assertThat(userAfterUpdate.accessExpiresOn).isEqualTo(originalAccessExpiresOn)
        }

        private fun setupSampleUserBeforeOnboarding(userId: String): User {
            val user = UserFactory.sample(
                analyticsId = AnalyticsId(
                    value = "1234567"
                ),
                identity = IdentityFactory.sample(id = userId),
                profile = null,
                organisation = null,
                accessExpiresOn = null
            )

            saveUser(user)

            saveOrganisation(
                OrganisationFactory.school(
                    name = "San Fran Forest School",
                    address = Address(
                        state = State.fromCode("CA"),
                        country = Country.fromCode("USA")
                    )
                )
            )

            setSecurityContext(userId)
            return user
        }
    }

    @Nested
    inner class GetUser {
        @Test
        fun `get own profile as teacher`() {
            val organisation = saveOrganisation(school(address = Address(country = Country.usa(), state = State.fromCode("WA"))))
            val user = saveUser(UserFactory.sample(organisation = organisation))

            mvc.perform(
                get("/v1/users/${user.id.value}").asTeacher(user.id.value)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.lastName").exists())
                .andExpect(jsonPath("$.analyticsId").exists())
                .andExpect(jsonPath("$.organisation.name").exists())
                .andExpect(jsonPath("$.organisation.state").exists())
                .andExpect(jsonPath("$.organisation.country").exists())
                .andExpect(jsonPath("$._links.self.href", endsWith("/users/${user.id.value}")))
                .andExpect(jsonPath("$._links.accessRules").doesNotExist())
        }

        @Test
        fun `get own profile as api user`() {
            val user = saveUser(UserFactory.sample())

            mvc.perform(
                get("/v1/users/${user.id.value}").asApiUser(user.id.value)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.analyticsId").doesNotExist())
                .andExpect(jsonPath("$.organisation").doesNotExist())
                .andExpect(jsonPath("$.teacherPlatformAttributes").doesNotExist())
        }

        @Test
        fun `get own profile as teacher and api user`() {
            val organisation = saveOrganisation(school())
            val user = saveUser(UserFactory.sample(organisation = organisation))

            mvc.perform(
                get("/v1/users/${user.id.value}").asUserWithRoles(
                    user.id.value,
                    UserRoles.ROLE_TEACHER,
                    UserRoles.ROLE_API
                )
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.analyticsId").exists())
                .andExpect(jsonPath("$.organisation").exists())
                .andExpect(jsonPath("$.teacherPlatformAttributes").exists())
        }

        @Test
        fun `get user that does not exist`() {
            mvc.perform(
                get("/v1/users/rafal").asUserWithRoles("ben", UserRoles.VIEW_USERS)
            )
                .andExpect(status().isNotFound)
        }

        @Test
        fun `Boclips service is able to retrieve user information and see their organisation and links`() {
            val organisationAccount = saveOrganisation(OrganisationFactory.apiIntegration())
            val user = saveUser(UserFactory.sample(organisation = organisationAccount))

            mvc.perform(
                get("/v1/users/${user.id.value}").asBoclipsService()
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.organisation.id").exists())
                .andExpect(jsonPath("$.organisation.name", equalTo(organisationAccount.name)))
                .andExpect(
                    jsonPath(
                        "$.organisation.allowsOverridingUserIds",
                        equalTo(organisationAccount.allowsOverridingUserIds)
                    )
                )
                .andExpect(jsonPath("$._links.self.href", endsWith("users/${user.id.value}")))
                .andExpect(jsonPath("$._links.profile.href", endsWith("users/${user.id.value}")))
        }
    }

    @Test
    fun `synchronise identities`() {
        mvc.perform(post("/v1/users/sync-identities").asBackofficeUser())
            .andExpect(status().isOk)
    }

    @Test
    fun `synchronise crm profiles`() {
        mvc.perform(post("/v1/users/sync").asBackofficeUser())
            .andExpect(status().isOk)
    }

    @Nested
    inner class AccessRules {
        @Test
        fun `returns forbidden status when lacking correct role`() {
            val user = saveUser(UserFactory.sample())

            mvc.perform(get("/v1/users/${user.id.value}/access-rules")).andExpect(status().isForbidden)
        }

        @Test
        fun `can get the access rules assigned to a user`() {
            val collectionsAccessRule = AccessRule.IncludedCollections(
                name = "Test collections contract",
                collectionIds = listOf(CollectionId(("test-collection-id"))),
                id = AccessRuleId(ObjectId.get().toHexString())
            )
            accessRuleRepository.save(collectionsAccessRule)

            val videosAccessRule = AccessRule.IncludedVideos(
                name = "Test videos contract",
                videoIds = listOf(VideoId("test-video-id")),
                id = AccessRuleId(ObjectId.get().toHexString())
            )
            accessRuleRepository.save(videosAccessRule)

            val contentPackageId = ContentPackageId(ObjectId.get().toHexString())
            val contentPackage = ContentPackage(
                name = "Package 1",
                id = contentPackageId,
                accessRuleIds = listOf(collectionsAccessRule.id, videosAccessRule.id)
            )

            saveContentPackage(contentPackage)
            val organisation =
                saveOrganisation(OrganisationFactory.apiIntegration(deal = deal(contentPackageId = contentPackage.id)))
            val user = saveUser(UserFactory.sample(organisation = organisation))

            mvc.perform(
                get("/v1/users/${user.id.value}/access-rules").asUserWithRoles(
                    user.id.value,
                    UserRoles.VIEW_ACCESS_RULES
                )
            )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.accessRules", hasSize<Int>(2)))
                .andExpect(
                    jsonPath(
                        "$._embedded.accessRules[*].type",
                        containsInAnyOrder("IncludedCollections", "IncludedVideos")
                    )
                )
                .andExpect(
                    jsonPath(
                        "$._embedded.accessRules[*].name",
                        containsInAnyOrder("Test collections contract", "Test videos contract")
                    )
                )
                .andExpect(
                    jsonPath(
                        "$._embedded.accessRules[*]._links.self.href",
                        containsInAnyOrder(
                            endsWith("/v1/access-rules/${collectionsAccessRule.id.value}"),
                            endsWith("/v1/access-rules/${videosAccessRule.id.value}")
                        )
                    )
                )
        }

        @Test
        fun `assigns a new user a access rules based on a keycloak role`() {
            val userId = "709f86bf-3292-4c96-9c84-5c89a255a07c"
            val authority = "TEST_ORGANISATION"
            val organisationMatchingRole = "ROLE_$authority"
            keycloakClientFake.createAccount(
                Identity(
                    id = UserId(userId),
                    username = "service-account@somewhere.com",
                    roles = listOf(organisationMatchingRole),
                    createdAt = ZonedDateTime.now()
                )
            )

            val contentPackage = saveContentPackage(ContentPackageFactory.sample())

            val organisation = saveOrganisation(
                OrganisationFactory.apiIntegration(
                    deal = deal(
                        contentPackageId = contentPackage.id
                    ),
                    role = organisationMatchingRole
                )
            )

            mvc.perform(
                get("/v1/users/$userId/access-rules").asUserWithRoles(
                    userId,
                    UserRoles.VIEW_ACCESS_RULES,
                    authority
                )
            )
                .andExpect(status().isOk)

            val importedUser = userRepository.findById(
                UserId(
                    userId
                )
            )

            assertThat(importedUser).isNotNull
            assertThat(importedUser!!.organisation).isEqualTo(organisation)
        }
    }

    @Nested
    inner class ShareCode {
        @Test
        fun `returns 200 if the provided shareCode matches the user's shareCode`() {
            val validShareCode = "TEST"
            val user = saveUser(
                UserFactory.sample(
                    teacherPlatformAttributes = TeacherPlatformAttributesFactory.sample(shareCode = validShareCode)
                )
            )

            mvc.perform(get("/v1/users/${user.id.value}/shareCode/${validShareCode}")).andExpect(status().isOk)
        }

        @Test
        fun `returns 403 if the provided shareCode does not match the user's shareCode`() {
            val invalidShareCode = "TEST"
            val user = saveUser(
                UserFactory.sample(
                    teacherPlatformAttributes = TeacherPlatformAttributesFactory.sample(shareCode = "ABCD")
                )
            )

            mvc.perform(get("/v1/users/${user.id.value}/shareCode/${invalidShareCode}")).andExpect(status().isForbidden)
        }

        @Test
        fun `returns a 404 if user not found`() {
            mvc.perform(get("/v1/users/9999/shareCode/ABCD")).andExpect(status().isNotFound)
        }

        @Test
        fun `returns a 404 if user does not have shareCode set up`() {
            val user = saveUser(
                UserFactory.sample(
                    teacherPlatformAttributes = TeacherPlatformAttributesFactory.sample(shareCode = null)
                )
            )
            mvc.perform(get("/v1/users/${user.id.value}/shareCode/ABCD")).andExpect(status().isNotFound)
        }
    }
}
