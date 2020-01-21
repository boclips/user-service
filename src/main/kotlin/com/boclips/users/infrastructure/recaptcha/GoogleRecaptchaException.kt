package com.boclips.users.infrastructure.recaptcha

class GoogleRecaptchaException(message: String, cause: Exception?) : RuntimeException(message, cause) {
    constructor(message: String) : this(message, null)
}
