package com.boclips.users.domain.model.school

import com.boclips.users.domain.model.Countries

data class Country(
    val id: String,
    val name: String,
    val states: List<State>? = null
) {

    fun isUSA(): Boolean {
        return id == USA_ISO
    }

    override fun toString(): String {
        return "Country(id='$id', name='$name')"
    }

    companion object {
        const val USA_ISO = "USA"
        fun usa() = fromCode(USA_ISO)

        fun fromCode(countryCode: String): Country {
            return Countries.getByCode(countryCode)
                ?: throw IllegalStateException("Could not find three letter country code for $countryCode")
        }
    }
}
