package com.boclips.users.domain.model

sealed class OrganisationType {
    object ApiCustomer : OrganisationType()
    object District : OrganisationType()
    object School : OrganisationType()
}