package com.boclips.users.api.request

data class PageRenderedEventRequest(
    val url: String,
    val viewport: Viewport?,
    val isResize: Boolean?
)

data class Viewport(
    val width: Int,
    val height: Int
)
