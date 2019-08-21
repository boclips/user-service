package com.boclips.users.domain.model

import org.apache.commons.validator.routines.EmailValidator

data class Account(
    val id: UserId,
    val username: String,
    val platform: Platform
) {
    fun isBoclipsEmployee() = this.username.endsWith("@boclips.com")
    val email get() = if (EmailValidator.getInstance().isValid(this.username)) this.username else null
}
