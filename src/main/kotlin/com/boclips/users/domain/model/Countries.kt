package com.boclips.users.domain.model

import com.boclips.users.domain.model.school.Country
import java.util.Locale

object Countries {
    private val countriesByCode: Map<String, Country> = loadCountries()

    fun getByCode(code: String): Country? {
        return countriesByCode.get(code)
    }

    fun getAll(): List<Country> {
        return countriesByCode.values.toList()
    }

    private fun loadCountries(): Map<String, Country> {
        val isoCountries: List<String> = Locale.getISOCountries().toList()

        return isoCountries
            .map { country ->
                val locale = Locale("en", country)
                val code = locale.isO3Country
                val displayName = locale.displayCountry

                code to Country(id = code, name = displayName)
            }
            .toMap()
    }
}