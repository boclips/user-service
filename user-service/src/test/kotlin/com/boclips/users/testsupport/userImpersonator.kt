package com.boclips.users.testsupport

import com.boclips.users.config.security.UserRoles
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

fun MockHttpServletRequestBuilder.asUser(id: String) =
    this.with(SecurityMockMvcRequestPostProcessors.user(id))

fun MockHttpServletRequestBuilder.asUserWithRoles(id: String, vararg roles: String) =
    this.with(
        SecurityMockMvcRequestPostProcessors
            .user(id)
            .roles(*roles)
    )

fun MockHttpServletRequestBuilder.asHqUser() =
    this.with(
        SecurityMockMvcRequestPostProcessors
            .user("user-service")
            .roles(
                UserRoles.SYNCHRONIZE_USERS_HUBSPOT,
                UserRoles.SYNCHRONIZE_USERS_KEYCLOAK,
                UserRoles.INSERT_CONTENT_PACKAGES,
                UserRoles.VIEW_CONTENT_PACKAGES
            )
    )

fun MockHttpServletRequestBuilder.asPublisher(userId: String = "publisher@example.com") =
    this.with(
        SecurityMockMvcRequestPostProcessors
            .user(userId)
            .roles(UserRoles.ROLE_PUBLISHER)
    )

fun MockHttpServletRequestBuilder.asTeacher(userId: String = "teacher@example.com") =
    this.with(
        SecurityMockMvcRequestPostProcessors
            .user(userId)
            .roles(UserRoles.ROLE_TEACHER)
    )

fun MockHttpServletRequestBuilder.asBoclipsService(userId: String = "boclips-service") =
    this.with(
        SecurityMockMvcRequestPostProcessors
            .user(userId)
            .roles(UserRoles.ROLE_BOCLIPS_SERVICE, UserRoles.VIEW_USERS, UserRoles.VIEW_ORGANISATIONS)
    )

fun MockHttpServletRequestBuilder.asApiUser(userId: String = "api@example.com") =
    this.with(
        SecurityMockMvcRequestPostProcessors
            .user(userId)
            .roles(UserRoles.ROLE_API)
    )

fun MockHttpServletRequestBuilder.asOperator() =
    this.with(
        SecurityMockMvcRequestPostProcessors
            .user("operator")
            .roles(
                UserRoles.UPDATE_USERS,
                UserRoles.BROADCAST_EVENTS
            )
    )
