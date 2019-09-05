package com.boclips.users.presentation.hateoas

import org.springframework.hateoas.Link
import org.springframework.hateoas.ResourceSupport

/**
 * Spring HATEOAS does not play very well with JSON polymorphic serialisation. I've given up after a couple of hours
 * of trying to make it work and fallen back to this workaround.
 *
 * It correctly produces a wrapper with _embedded and _links properties, but feel free to hunt me down if tou feel
 * this is too awful.
 */
open class ContractResourcesHateoasWrapper(
    val _embedded: ContractResourcesWrapper,
    resourceLinks: List<Link> = emptyList()
) : ResourceSupport() {
    init {
        this.add(resourceLinks)
    }
}