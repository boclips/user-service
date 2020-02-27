package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.accessrules.AccessRuleId
import com.boclips.users.domain.model.account.Organisation
import com.boclips.users.domain.model.account.OrganisationId
import com.boclips.users.domain.model.account.DealType
import com.boclips.users.domain.model.account.ApiIntegration
import com.boclips.users.domain.model.account.District
import com.boclips.users.domain.model.account.OrganisationDetails
import com.boclips.users.domain.model.account.School
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import org.bson.types.ObjectId
import java.time.ZonedDateTime

class OrganisationAccountFactory {
    companion object {
        fun sample(
            id: OrganisationId = OrganisationId(value = ObjectId().toHexString()),
            type: DealType = DealType.STANDARD,
            accessRuleIds: List<AccessRuleId> = emptyList(),
            organisationDetails: OrganisationDetails = OrganisationDetailsFactory.school(),
            accessExpiresOn: ZonedDateTime? = null
        ): Organisation<*> {
            return Organisation(
                id = id,
                type = type,
                accessRuleIds = accessRuleIds,
                organisation = organisationDetails,
                accessExpiresOn = accessExpiresOn
            )
        }
    }
}

class OrganisationDetailsFactory {
    companion object {
        fun school(
            name: String = "Amazing Organisation",
            externalId: String = "externalId",
            country: Country = Country.fromCode(Country.USA_ISO),
            countryName: String? = null,
            postCode: String? = null,
            state: State = State.fromCode("IL"),
            district: Organisation<District>? = null
            ): School {
            return School(
                name = name,
                externalId = externalId,
                country = countryName?.let { Country.fromCode(it) } ?: country,
                state = state,
                postcode = postCode,
                district = district
            )
        }

        fun district(
            name: String = "Amazing Organisation",
            externalId: String = "externalId",
            state: State = State.fromCode("IL")
        ): District {
            return District(
                name = name,
                externalId = externalId,
                state = state
            )
        }

        fun apiIntegration(
            name: String = "Amazing Organisation",
            country: Country = Country.fromCode(Country.USA_ISO),
            state: State = State.fromCode("IL"),
            allowsOverridingUserIds: Boolean = false
        ): ApiIntegration {
            return ApiIntegration(
                name = name,
                country = country,
                state = state,
                allowsOverridingUserIds = allowsOverridingUserIds
            )
        }
    }
}
