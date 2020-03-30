package com.boclips.users.application.exceptions

import com.boclips.eventbus.domain.video.VideoType
import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

class InvalidVideoTypeException(videoType: String) : BoclipsApiException(
    ExceptionDetails(
        error = "Invalid videoType",
        message = "$videoType is not a valid type, valid types are: ${VideoType.values()}}",
        status = HttpStatus.BAD_REQUEST
    )
)
