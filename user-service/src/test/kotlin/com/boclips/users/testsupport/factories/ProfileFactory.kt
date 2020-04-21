package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.Profile
import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.organisation.Organisation
import com.boclips.users.domain.model.organisation.School

class ProfileFactory {
    companion object {
        fun sample(
            subjects: List<Subject> = emptyList(),
            ages: List<Int> = listOf(1, 2),
            firstName: String = "Joe",
            lastName: String = "Dough",
            hasOptedIntoMarketing: Boolean = true,
            role: String? = "TEACHER",
            school: Organisation<School>? = null
        ) = Profile(
            subjects = subjects,
            ages = ages,
            firstName = firstName,
            lastName = lastName,
            hasOptedIntoMarketing = hasOptedIntoMarketing,
            role = role,
            school = school
        )
    }
}
