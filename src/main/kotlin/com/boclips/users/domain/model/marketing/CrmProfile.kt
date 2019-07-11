package com.boclips.users.domain.model.marketing

import com.boclips.users.domain.model.Subject
import com.boclips.users.domain.model.UserId
import org.apache.commons.validator.routines.EmailValidator
import java.time.Instant

class CrmProfile(
    val id: UserId,
    val activated: Boolean,
    val subjects: List<Subject>,
    val ageRange: List<Int>,
    val firstName: String,
    val lastName: String,
    val email: String,
    val hasOptedIntoMarketing: Boolean,
    val lastLoggedIn: Instant?,
    val marketingTracking: MarketingTracking
) {
    fun isValid(): Boolean {
        return this.firstName.isNotEmpty() &&
            this.lastName.isNotEmpty() &&
            this.email.isNotEmpty() &&
            EmailValidator.getInstance().isValid(this.email)
    }

    override fun toString(): String {
        return "CrmProfile(id=$id)"
    }
}