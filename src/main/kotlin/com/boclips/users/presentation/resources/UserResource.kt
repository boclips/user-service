package com.boclips.users.presentation.resources

import org.springframework.hateoas.Identifiable

class UserResource(
    private val id: String

) : Identifiable<String> {
    override fun getId() = id
}