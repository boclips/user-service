package com.boclips.users.domain.service.organisation

import com.boclips.users.domain.model.organisation.ExternalOrganisationInformation
import com.boclips.users.domain.model.organisation.ExternalSchoolInformation

interface AmericanSchoolsProvider {
    fun lookupSchools(stateId: String, schoolName: String): List<ExternalOrganisationInformation>
    fun fetchSchool(schoolId: String): ExternalSchoolInformation?
}
