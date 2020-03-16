package com.boclips.users.application.commands

import com.boclips.users.domain.model.school.State
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
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        value ?: return true
        return State.states().map { it.id }.contains(value)
    }
}
