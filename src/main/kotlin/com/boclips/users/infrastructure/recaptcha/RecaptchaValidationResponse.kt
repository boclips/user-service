package com.boclips.users.infrastructure.recaptcha

import com.fasterxml.jackson.annotation.JsonProperty

data class RecaptchaValidationResponse(
    val success: Boolean,
    val challenge_ts: String?,
    val hostname: String?,
    val score: Double?,
    val action: String?,
    @JsonProperty("error-codes")
    val errorCodes: Array<String>?
) {
//    public var errorCodes: Array<String>? = null
//
//    @Suppress("unused")
//    @JsonProperty("error-codes")
//    fun setErrorCodes(errorCodes: Array<String>?) {
//        this.errorCodes = errorCodes
//    }
}