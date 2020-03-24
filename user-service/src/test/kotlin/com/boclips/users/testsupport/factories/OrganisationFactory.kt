package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.contentpackage.ContentPackageId
import com.boclips.users.domain.model.organisation.ApiIntegration
import com.boclips.users.domain.model.organisation.DealType
import com.boclips.users.domain.model.organisation.District
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.OrganisationDetails
import com.boclips.users.domain.model.organisation.OrganisationId
import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State
import org.bson.types.ObjectId
import java.time.ZonedDateTime

class OrganisationFactory {
    companion object {
        fun <T : OrganisationDetails> sample(
            id: OrganisationId = OrganisationId(value = ObjectId().toHexString()),
            type: DealType = DealType.STANDARD,
            details: T,
            accessExpiresOn: ZonedDateTime? = null,
            role: String? = "SAMPLE_ROLE",
            contentPackageId: ContentPackageId? = null
        ): Organisation<T> {
            return Organisation(
                id = id,
                type = type,
                details = details,
                accessExpiresOn = accessExpiresOn,
                role = role,
                contentPackageId = contentPackageId
            )
        }

        fun school(
            id: OrganisationId = OrganisationId(value = ObjectId().toHexString()),
            type: DealType = DealType.STANDARD,
            school: School = OrganisationDetailsFactory.school(),
            accessExpiresOn: ZonedDateTime? = null,
            role: String? = "SAMPLE_ROLE"
        ): Organisation<School> {
            return Organisation(
                id = id,
                type = type,
                details = school,
                accessExpiresOn = accessExpiresOn,
                role = role
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
            domain: String? = null,
            externalId: String = "externalId",
            state: State = State.fromCode("IL")
        ): District {
            return District(
                name = name,
                domain = domain,
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