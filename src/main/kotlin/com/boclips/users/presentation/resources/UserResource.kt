package com.boclips.users.presentation.resources

open class UserResource(
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val ages: List<Int>?,
    val subjects: List<SubjectResource>?,
    val email: String?,
    val analyticsId: String?,
    val organisationAccountId: String?,
    val organisation: OrganisationResource?
)
