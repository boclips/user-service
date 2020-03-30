package com.boclips.users.application.exceptions

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

class DuplicateContentPackageException(name: String) : BoclipsApiException(
    ExceptionDetails(
        error = "Duplicate content package",
        message = "$name content package already exists. Please choose a different name",
        status = HttpStatus.CONFLICT
    )
)
