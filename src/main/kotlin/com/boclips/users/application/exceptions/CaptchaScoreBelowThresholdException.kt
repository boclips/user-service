package com.boclips.users.application.exceptions

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

class CaptchaScoreBelowThresholdException(identifier: String) : BoclipsApiException(
    ExceptionDetails(
        error = HttpStatus.BAD_REQUEST.reasonPhrase,
        message = "User $identifier has a captcha score below the configured threshold",
        status = HttpStatus.BAD_REQUEST
    )
)
