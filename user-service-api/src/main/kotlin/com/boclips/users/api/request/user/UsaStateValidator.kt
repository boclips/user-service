package com.boclips.users.api.request.user

import java.util.TreeMap
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.ReportAsSingleViolation
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [UsaStateValidator::class])
@Target(
    AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.PROPERTY_GETTER
)
@Retention(AnnotationRetention.RUNTIME)
@ReportAsSingleViolation
annotation class UsaState(
    val message: String = "Invalid USA state code",
    val groups: Array<KClass<out Any>> = [],
    val payload: Array<KClass<out Any>> = []
)

class UsaStateValidator : ConstraintValidator<UsaState, String> {
    companion object {
        fun getAllStates(): Map<String, String> {
            val states = TreeMap<String, String>()
            states["AL"] = "Alabama"
            states["AK"] = "Alaska"
            states["AZ"] = "Arizona"
            states["AR"] = "Arkansas"
            states["CA"] = "California"
            states["CO"] = "Colorado"
            states["CT"] = "Connecticut"
            states["DE"] = "Delaware"
            states["FL"] = "Florida"
            states["GA"] = "Georgia"
            states["HI"] = "Hawaii"
            states["ID"] = "Idaho"
            states["IL"] = "Illinois"
            states["IN"] = "Indiana"
            states["IA"] = "Iowa"
            states["KS"] = "Kansas"
            states["KY"] = "Kentucky[E]"
            states["LA"] = "Louisiana"
            states["ME"] = "Maine"
            states["MD"] = "Maryland"
            states["MA"] = "Massachusetts[E]"
            states["MI"] = "Michigan"
            states["MN"] = "Minnesota"
            states["MS"] = "Mississippi"
            states["MO"] = "Missouri"
            states["MT"] = "Montana"
            states["NE"] = "Nebraska"
            states["NV"] = "Nevada"
            states["NH"] = "New Hampshire"
            states["NJ"] = "New Jersey"
            states["NM"] = "New Mexico"
            states["NY"] = "New York"
            states["NC"] = "North Carolina"
            states["ND"] = "North Dakota"
            states["OH"] = "Ohio"
            states["OK"] = "Oklahoma"
            states["OR"] = "Oregon"
            states["PA"] = "Pennsylvania"
            states["RI"] = "Rhode Island"
            states["SC"] = "South Carolina"
            states["SD"] = "South Dakota"
            states["TN"] = "Tennessee"
            states["TX"] = "Texas"
            states["UT"] = "Utah"
            states["VT"] = "Vermont"
            states["VA"] = "Virginia[E]"
            states["WA"] = "Washington"
            states["WV"] = "West Virginia"
            states["WI"] = "Wisconsin"
            states["WY"] = "Wyoming"

            return states
        }
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        value ?: return true
        return getAllStates().contains(value)
    }
}
