package com.boclips.users.testsupport.factories

import com.boclips.users.domain.model.Profile
import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.SubjectId
import com.boclips.users.domain.model.school.Country
import com.boclips.users.domain.model.school.State

class ProfileFactory {
    companion object {
        fun sample(
            subjects: List<Subject> = listOf(
                Subject(
                    id = SubjectId(value = "1"),
                    name = "Maths"
                )
            ),
            ages: List<Int> = listOf(1, 2),
            firstName: String = "Joe",
            lastName: String = "Dough",
            hasOptedIntoMarketing: Boolean = true,
            country: Country? = Country(id = "POL", name = "Poland"),
            state: State? = State(id = "NY", name = "New York"),
            school: String = "Brooklyn School"
        ) = Profile(
            subjects = subjects,
            ages = ages,
            firstName = firstName,
            lastName = lastName,
            hasOptedIntoMarketing = hasOptedIntoMarketing,
            country = country,
            state = state,
            school = school
        )
    }
}
