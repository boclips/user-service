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

fun MockHttpServletRequestBuilder.asBackofficeUser() =
    this.with(
        SecurityMockMvcRequestPostProcessors
            .user("user-service")
            .roles(UserRoles.SYNCHRONIZE_USERS_HUBSPOT, UserRoles.SYNCHRONIZE_USERS_KEYCLOAK)
    )