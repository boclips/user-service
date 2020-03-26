package com.boclips.users.presentation.converters

import com.boclips.users.api.response.state.StateResource
import com.boclips.users.api.response.state.StatesResource
import com.boclips.users.api.response.state.StatesWrapperResource
import com.boclips.users.domain.model.school.State
import com.boclips.users.presentation.hateoas.CountryLinkBuilder
import org.springframework.stereotype.Component

@Component
class StateConverter(private val countryLinkBuilder: CountryLinkBuilder) {
    fun toStateResources(states: List<State>?): List<StateResource> {
        return states?.map {
            StateResource(id = it.id, name = it.name)
        } ?: emptyList()
    }

    fun toStatesResource(states: List<State>?): StatesResource {
        return StatesResource(
            _embedded = StatesWrapperResource(
                states = states?.map {
                    StateResource(
                        id = it.id,
                        name = it.name
                    )
                } ?: emptyList()
            ),
            _links = listOfNotNull(countryLinkBuilder.getUsStatesSelfLink())
                .map { it.rel.value() to it }.toMap()
        )
    }
}
