package com.boclips.users.api.response.user

import com.boclips.users.api.BoclipsServiceProjection
import com.boclips.users.api.TeacherProjection
import com.boclips.users.api.UserProjection
import com.boclips.users.api.response.organisation.OrganisationDetailsResource
import com.boclips.users.api.response.SubjectResource
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonView
import org.springframework.hateoas.Link

data class UserResource(
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
    val organisation: OrganisationDetailsResource?,
    @get:JsonView(TeacherProjection::class, BoclipsServiceProjection::class)
    val school: OrganisationDetailsResource?,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var _links: Map<String, Link>? = null
)
