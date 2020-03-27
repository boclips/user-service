package com.boclips.users.api.request.user

import java.util.TreeMap
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.ReportAsSingleViolation
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [RoleValidator::class])
@Target(
    AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.PROPERTY_GETTER
)
@Retention(AnnotationRetention.RUNTIME)
@ReportAsSingleViolation
annotation class Role(
    val message: String = "Role must be TEACHER, PARENT, SCHOOLADMIN, or OTHER",
    val groups: Array<KClass<out Any>> = [],
    val payload: Array<KClass<out Any>> = []
)

class RoleValidator : ConstraintValidator<Role, String> {

    enum class RoleTypes {
        TEACHER,
        PARENT,
        SCHOOLADMIN,
        OTHER
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        value ?: return true
        return RoleTypes.values().any { it.name == value }
    }
}
