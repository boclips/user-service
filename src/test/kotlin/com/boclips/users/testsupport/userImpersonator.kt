package com.boclips.users.testsupport

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

fun MockHttpServletRequestBuilder.asUser(id: String) =
    this.with(SecurityMockMvcRequestPostProcessors.user(id))
