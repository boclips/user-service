package com.boclips.users.application

import com.boclips.users.domain.service.UserUpdateCommand
import com.boclips.users.presentation.requests.UpdateUserRequest
import org.springframework.stereotype.Component

@Component
class UserUpdatesConverter {
    fun convert(updateUserRequest: UpdateUserRequest): List<UserUpdateCommand> {
        return listOfNotNull(
            updateUserRequest.firstName?.let { UserUpdateCommand.ReplaceFirstName(firstName = it)},
            updateUserRequest.lastName?.let { UserUpdateCommand.ReplaceLastName(lastName = it)},
            updateUserRequest.subjects?.let { UserUpdateCommand.ReplaceSubjects(subjects = it)},
            updateUserRequest.ages?.let { UserUpdateCommand.ReplaceAges(ages = it)},
            updateUserRequest.hasOptedIntoMarketing?.let { UserUpdateCommand.ReplaceHasOptedIntoMarketing(hasOptedIntoMarketing = it)}
        )
    }
}