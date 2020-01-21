package com.boclips.users.application.exceptions

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

class PermissionDeniedException : BoclipsApiException(
    ExceptionDetails(
        error = HttpStatus.FORBIDDEN.reasonPhrase,
        message = "Permission denied",
        status = HttpStatus.FORBIDDEN
    )
)
