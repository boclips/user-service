package com.boclips.users.domain.service

import com.boclips.users.domain.model.Subject

sealed class UserUpdateCommand {
    data class ReplaceFirstName(val firstName: String) : UserUpdateCommand()
    data class ReplaceLastName(val lastName: String) : UserUpdateCommand()
    data class ReplaceSubjects(val subjects: List<Subject>) : UserUpdateCommand()
    data class ReplaceAges(val ages: List<Int>) : UserUpdateCommand()
    data class ReplaceHasOptedIntoMarketing(val hasOptedIntoMarketing: Boolean) : UserUpdateCommand()
}
