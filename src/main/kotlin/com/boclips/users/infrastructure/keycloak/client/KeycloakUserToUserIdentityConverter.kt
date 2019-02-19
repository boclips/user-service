package com.boclips.users.infrastructure.keycloak.client

import com.boclips.users.domain.model.identity.Identity
import com.boclips.users.domain.model.identity.IdentityId
import com.boclips.users.infrastructure.keycloak.client.exceptions.InvalidUserRepresentation
import org.apache.commons.validator.routines.EmailValidator
import org.keycloak.representations.idm.UserRepresentation

class KeycloakUserToUserIdentityConverter {

    private val emailValidator = EmailValidator.getInstance()

    fun convert(userRepresentation: UserRepresentation): Identity {
        return Identity(
            id = IdentityId(value = getValueIfValid("id", userRepresentation.id)),
            email = getEmailIfValid(userRepresentation.email),
            firstName = getValueIfValid("firstName", userRepresentation.firstName),
            lastName = getValueIfValid("lastName", userRepresentation.lastName),
            isVerified = userRepresentation.isEmailVerified
        )
    }

    private fun getEmailIfValid(email: String?): String {
        if (!emailValidator.isValid(email)) {
            throw InvalidUserRepresentation("invalid email")
        }

        return email!!
    }

    private fun getValueIfValid(fieldName: String, fieldValue: String?): String {
        if (fieldValue.isNullOrBlank()) {
            throw InvalidUserRepresentation("missing $fieldName")
        }

        return fieldValue
    }
}