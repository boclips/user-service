package com.boclips.users.infrastructure.keycloak.client

import com.boclips.users.domain.model.UserId
import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.infrastructure.keycloak.UnknownUserSourceException
import com.boclips.users.infrastructure.keycloak.client.exceptions.InvalidUserRepresentation
import com.boclips.users.infrastructure.organisation.UserSourceResolver
import org.apache.commons.validator.routines.EmailValidator
import org.keycloak.representations.idm.UserRepresentation

class KeycloakUserToIdentityConverter(
    private val userSourceResolver: UserSourceResolver
) {
    private val emailValidator = EmailValidator.getInstance()

    fun convert(userRepresentation: UserRepresentation): Identity {
        val userRole = userSourceResolver.resolve(userRepresentation.realmRoles)
            ?: throw UnknownUserSourceException("Could not resolve roles: ${userRepresentation.realmRoles}")
        val userId = userRepresentation.id

        if (userId.isEmpty()) throw IllegalStateException()

        return Identity(
            id = UserId(value = userId),
            email = getEmailIfValid(userRepresentation.email),
            isVerified = userRepresentation.isEmailVerified,
            associatedTo = userRole
        )
    }

    private fun getEmailIfValid(email: String?): String {
        if (!emailValidator.isValid(email)) {
            throw InvalidUserRepresentation("invalid email")
        }

        return email!!
    }
}
