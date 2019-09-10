package com.boclips.users.presentation.resources.school

import com.boclips.users.domain.model.school.State
import org.springframework.hateoas.Resource
import org.springframework.stereotype.Component

@Component
class StateConverter{
    fun toStatesResource(states: List<State>?): List<Resource<StateResource>>? {
        return states?.map {
            Resource(
                StateResource(id = it.id, name = it.name)
            )
        }
    }
}