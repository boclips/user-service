package com.boclips.users.application.commands

import com.boclips.users.domain.model.school.State
import com.boclips.users.domain.model.school.State.Companion.states
import org.springframework.stereotype.Service

@Service
class GetUsStates {
    operator fun invoke(): List<State> {
        return states()
    }
}
