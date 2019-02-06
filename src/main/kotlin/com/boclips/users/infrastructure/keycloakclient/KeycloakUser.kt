package com.boclips.users.infrastructure.keycloakclient

import com.boclips.users.domain.model.users.User
import org.keycloak.representations.idm.UserRepresentation
import java.util.*

data class KeycloakUser(
        val username: String,
        val id: String? = null,
        val email: String? = null,
        val firstName: String? = null,
        val lastName: String? = null,
        val date: Date? = null,
        val timestamp: Long? = null,
        val emailVerified: Boolean = false,
        val mixpanelDistinctId: String? = null,
        val subjects: String? = null,
        val requiredActions: List<String> = emptyList()
) {
    companion object {
        fun from(user: UserRepresentation) =
                KeycloakUser(
                        username = user.username,
                        id = user.id,
                        email = user.email,
                        firstName = user.firstName,
                        lastName = user.lastName,
                        date = Date(user.createdTimestamp),
                        timestamp = user.createdTimestamp,
                        emailVerified = user.isEmailVerified,
                        mixpanelDistinctId = user.attributes?.get("mixpanelDistinctId")?.first(),
                        subjects = user.attributes?.get("subjects")?.first(),
                        requiredActions = user.requiredActions
                )

    }

    fun toKeycloakUserRepresentation(): UserRepresentation {
        val userRepresentation = UserRepresentation()
        userRepresentation.isEmailVerified = this.emailVerified
        userRepresentation.id = this.id
        userRepresentation.attributes = mapOf(
                "mixpanelDistinctId" to listOf(this.mixpanelDistinctId),
                "subjects" to listOf(this.subjects)
        )
        userRepresentation.firstName = this.firstName
        userRepresentation.lastName = this.lastName
        userRepresentation.requiredActions = this.requiredActions
        userRepresentation.username = this.username
        userRepresentation.email = this.email

        return userRepresentation
    }

    @Throws(NullPointerException::class)
    fun toUser(): User = User(
            id = this.id!!,
            email = this.email,
            firstName = this.firstName,
            lastName = this.lastName,
            activated = this.emailVerified,
            mixpanelDistinctId = this.mixpanelDistinctId,
            subjects = this.subjects?.let { listOf(it) },
            createdDate = date
    )
}
