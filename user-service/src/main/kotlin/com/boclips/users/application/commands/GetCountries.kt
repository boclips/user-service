package com.boclips.users.application.commands

import com.boclips.users.domain.model.Countries
import com.boclips.users.domain.model.school.Country
import org.springframework.stereotype.Service

@Service
class GetCountries {
    operator fun invoke(): List<Country> {
        return Countries.getAll()
    }
}
