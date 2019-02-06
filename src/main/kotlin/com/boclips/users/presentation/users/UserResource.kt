package com.boclips.users.presentation.users

import org.springframework.hateoas.Identifiable

data class UserResource(
        private val id: String,
        val activated: Boolean
) : Identifiable<String> {
    override fun getId() = id
}