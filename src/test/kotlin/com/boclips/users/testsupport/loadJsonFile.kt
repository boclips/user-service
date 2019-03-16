package com.boclips.users.testsupport

import org.apache.commons.io.IOUtils
import org.springframework.util.ResourceUtils
import java.nio.charset.Charset

fun loadWireMockStub(fileName: String): String? {
    return IOUtils.toString(
        ResourceUtils.getFile("classpath:wiremock/$fileName").toURI(),
        Charset.defaultCharset()
    )
}