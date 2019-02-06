package com.boclips.users.testsupport

import com.boclips.users.config.security.UserRoles
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

fun MockHttpServletRequestBuilder.asUser(id: String) =
        this.with(SecurityMockMvcRequestPostProcessors.user(id))


fun MockHttpServletRequestBuilder.asOperator() =
        this.with(SecurityMockMvcRequestPostProcessors
                .user("operator")
                .roles(UserRoles.VIEW_USERS)
        )
