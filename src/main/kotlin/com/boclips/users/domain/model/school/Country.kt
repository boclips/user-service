package com.boclips.users.domain.model.school

import com.boclips.users.domain.model.Countries

data class Country(
    val id: String,
    val name: String
) {
    fun isUSA(): Boolean {
        return id == "USA"
    }

    companion object {
        fun fromCode(countryCode: String): Country {
            return Countries.getByCode(countryCode)?.let { it }
                ?: throw IllegalStateException("Could not find three letter country code for $countryCode")
        }
    }
}