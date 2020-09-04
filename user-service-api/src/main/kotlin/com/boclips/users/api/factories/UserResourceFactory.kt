package com.boclips.users.api.factories

import com.boclips.users.api.response.SubjectResource
import com.boclips.users.api.response.feature.FeaturesResource
import com.boclips.users.api.response.user.TeacherPlatformAttributesResource
import com.boclips.users.api.response.organisation.OrganisationDetailsResource
import com.boclips.users.api.response.user.UserResource

class UserResourceFactory {
    companion object {
        @JvmStatic
        fun sample(
            id: String = "123",
            firstName: String = "Bo",
            lastName: String = "Clips",
            ages: List<Int> = emptyList(),
            subjects: List<SubjectResource> = emptyList(),
            email: String = "bo@clips.com",
            analyticsId: String = "321",
            teacherPlatformAttributes: TeacherPlatformAttributesResource? = null,
            organisation: OrganisationDetailsResource? = null,
            school: OrganisationDetailsResource? = null,
            features: FeaturesResource? = null
        ): UserResource =
            UserResource(
                id = id,
                firstName = firstName,
                lastName = lastName,
                ages = ages,
                subjects = subjects,
                email = email,
                analyticsId = analyticsId,
                teacherPlatformAttributes = teacherPlatformAttributes,
                organisation = organisation,
                school = school,
                features = features
            )
    }
}
