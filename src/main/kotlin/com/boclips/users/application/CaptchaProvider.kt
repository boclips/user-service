package com.boclips.users.application

interface CaptchaProvider {
    fun validateCaptchaToken(token: String, identifier: String): Boolean
}
