package com.boclips.users.domain.model.school

import com.boclips.users.api.request.user.UsaStateValidator

data class State(
    val id: String,
    val name: String
) {
    companion object {
        fun fromCode(stateCode: String): State {
            statesAsMap().get(stateCode)?.let {
                return State(id = stateCode, name = it)
            } ?: throw IllegalStateException("Could not find US state $stateCode")
        }

        fun states(): List<State> = statesAsMap().keys.map(::fromCode)

        private fun statesAsMap(): Map<String, String> {
            return UsaStateValidator.getAllStates()
        }
    }
}
