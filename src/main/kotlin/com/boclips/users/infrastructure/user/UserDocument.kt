package com.boclips.users.infrastructure.user

import com.boclips.users.domain.model.users.User
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class UserDocument(
        @Id
        val id: String,
        val activated: Boolean
) {
    companion object {
        fun from(user: User) = UserDocument(
                id = user.id,
                activated = user.activated
        )
    }

    fun toUser() = User(id = id, activated = activated)
}