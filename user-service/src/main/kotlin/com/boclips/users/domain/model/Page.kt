package com.boclips.users.domain.model

data class Page<T>(
    val items: List<T>,
    val pageSize: Int,
    val pageNumber: Int,
    val totalElements: Long
) : Iterable<T> by items
