package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.contract.ContractId
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationAccount
import com.boclips.users.domain.model.organisation.OrganisationAccountId
import com.boclips.users.domain.model.organisation.OrganisationAccountType
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import org.bson.types.ObjectId
import java.time.ZonedDateTime

class OrganisationAccountFactory {
    companion object {
        fun sample(
            id: OrganisationAccountId = OrganisationAccountId(value = ObjectId().toHexString()),
            type: OrganisationAccountType = OrganisationAccountType.STANDARD,
            contractIds: List<ContractId> = emptyList(),
            organisation: Organisation = OrganisationFactory.school()
        ): OrganisationAccount<*> {
            return OrganisationAccount(
                id = id,
                type = type,
                contractIds = contractIds,
                organisation = organisation
            )
        }
    }
}

class OrganisationFactory {
    companion object {
        fun school(
            name: String = "Amazing Organisation",
            externalId: String = "externalId",
            country: Country = Country.fromCode(Country.USA_ISO),
            countryName: String? = null,
            postCode: String? = null,
            state: State = State.fromCode("IL"),
            district: OrganisationAccount<District>? = null,
            accessExpiry: ZonedDateTime? = null
        ): School {
            return School(
                name = name,
                externalId = externalId,
                country = countryName?.let { Country.fromCode(it) } ?: country,
                state = state,
                postcode = postCode,
                district = district,
                accessExpiry = accessExpiry
            )
        }

        fun district(
            name: String = "Amazing Organisation",
            externalId: String = "externalId",
            state: State = State.fromCode("IL"),
            accessExpiry: ZonedDateTime? = null
        ): District {
            return District(
                name = name,
                externalId = externalId,
                state = state,
                accessExpiry = accessExpiry
            )
        }

        fun apiIntegration(
            name: String = "Amazing Organisation",
            country: Country = Country.fromCode(Country.USA_ISO),
            state: State = State.fromCode("IL")
        ): ApiIntegration {
            return ApiIntegration(
                name = name,
                country = country,
                state = state
            )
        }
    }
}
