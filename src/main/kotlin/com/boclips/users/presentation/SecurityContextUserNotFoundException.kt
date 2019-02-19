package com.boclips.users.presentation

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class SecurityContextUserNotFoundException : RuntimeException()
