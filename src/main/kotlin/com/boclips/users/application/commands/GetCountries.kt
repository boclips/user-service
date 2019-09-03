package com.boclips.users.application.commands

import com.boclips.users.domain.model.school.Country
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class GetCountries {
    operator fun invoke(): List<Country> {
        return Locale.getISOCountries().map { Locale("", it) }
            .map { Country(id = it.isO3Country, name = it.displayCountry) }
    }
}