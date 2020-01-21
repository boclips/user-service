package com.boclips.users.infrastructure

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

fun getContentTypeHeader(): HttpHeaders {
    val headers = HttpHeaders()
    headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE)
    return headers
}
