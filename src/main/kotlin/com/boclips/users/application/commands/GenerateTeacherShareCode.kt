package com.boclips.users.application.commands

import org.springframework.stereotype.Component

/* We excluded vowels from the share code generator
* to make sure that we do not create 'bad' words
*/
@Component
class GenerateTeacherShareCode {
    companion object {
        const val ALLOWED_CHARACTERS = "BCDFGHJKLMNPQRSTVWXYZ0123456789"
        const val LENGTH = 4
    }

    operator fun invoke(): String {
        return (1..LENGTH)
            .map { ALLOWED_CHARACTERS.random() }
            .joinToString("")
    }
}
