package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.organisation.School
import com.boclips.users.domain.model.subject.Subject
import com.boclips.users.domain.model.user.Profile

class ProfileFactory {
    companion object {
        fun sample(
            subjects: List<Subject> = emptyList(),
            ages: List<Int> = listOf(1, 2),
            firstName: String = "Joe",
            lastName: String = "Dough",
            hasOptedIntoMarketing: Boolean = true,
            role: String? = "TEACHER",
            school: School? = null
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
