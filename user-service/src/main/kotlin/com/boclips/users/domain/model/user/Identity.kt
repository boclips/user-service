package com.boclips.users.domain.model.user

import org.apache.commons.validator.routines.EmailValidator
import java.time.ZonedDateTime

data class Identity(
    val id: UserId,
    val username: String,
    val idpEmail: String? = null,
    val roles: List<String> = emptyList(),
    val createdAt: ZonedDateTime,
    val firstName: String? = null,
    val lastName: String? = null
) {
    fun isBoclipsEmployee() = this.username.endsWith("@boclips.com")
    val email
        get() = when {
            EmailValidator.getInstance().isValid(this.username) -> this.username
            EmailValidator.getInstance().isValid(this.idpEmail) -> this.idpEmail
            else -> null
        }
}
