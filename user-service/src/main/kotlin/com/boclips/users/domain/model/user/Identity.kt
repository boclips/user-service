package com.boclips.users.domain.model.user

import org.apache.commons.validator.routines.EmailValidator
import java.time.ZonedDateTime

data class Identity(
    val id: UserId,
    val username: String,
    val roles: List<String> = emptyList(),
    val createdAt: ZonedDateTime
) {
    fun isBoclipsEmployee() = this.username.endsWith("@boclips.com")
    val email get() = if (EmailValidator.getInstance().isValid(this.username)) this.username else null
}
