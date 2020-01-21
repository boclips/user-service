package com.boclips.users.presentation.resources

import com.boclips.users.presentation.projections.BoclipsServiceProjection
import com.boclips.users.presentation.projections.TeacherProjection
import com.boclips.users.presentation.projections.UserProjection
import com.fasterxml.jackson.annotation.JsonView
import org.springframework.hateoas.core.Relation

@Relation(collectionRelation = "user")
open class UserResource(
    @get:JsonView(UserProjection::class)
    val id: String,
    @get:JsonView(UserProjection::class)
    val firstName: String?,
    @get:JsonView(UserProjection::class)
    val lastName: String?,
    @get:JsonView(UserProjection::class)
    val ages: List<Int>?,
    @get:JsonView(UserProjection::class)
    val subjects: List<SubjectResource>?,
    @get:JsonView(UserProjection::class)
    val email: String?,
    @get:JsonView(TeacherProjection::class)
    val analyticsId: String?,
    @get:JsonView(TeacherProjection::class)
    val teacherPlatformAttributes: TeacherPlatformAttributesResource?,
    @get:JsonView(TeacherProjection::class, BoclipsServiceProjection::class)
    val organisationAccountId: String?,
    @get:JsonView(TeacherProjection::class, BoclipsServiceProjection::class)
    val organisation: OrganisationResource?
)
