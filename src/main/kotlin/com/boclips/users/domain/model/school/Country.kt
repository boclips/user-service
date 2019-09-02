package com.boclips.users.domain.model.school

import java.util.Locale
import java.util.TreeMap

data class Country(
    val id: String,
    val name: String
) {
    fun isUSA(): Boolean {
        return id == "USA"
    }

    companion object {
        fun fromCode(countryCode: String): Country {
            return countries().get(countryCode)?.let { it }
                ?: throw IllegalStateException("Could not find three letter country code for $countryCode")
        }

        private fun countries(): Map<String, Country> {
            val countriesByCode = TreeMap<String, Country>()
            val isoCountries = Locale.getISOCountries()
            for (country in isoCountries) {
                val locale = Locale("en", country)
                val code = locale.isO3Country
                val displayName = locale.displayCountry
                countriesByCode.put(code, Country(id = code, name = displayName))
            }
            return countriesByCode
        }
    }
}