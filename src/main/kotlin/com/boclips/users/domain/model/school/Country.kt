package com.boclips.users.domain.model.school

import java.util.Locale

data class Country(
    val id: String,
    val name: String
) {
    companion object {
        fun fromCode(countryCode: String): Country {
            val locale = Locale("", countryCode)
            return Country(id = locale.country, name = locale.displayCountry)
        }
    }
}