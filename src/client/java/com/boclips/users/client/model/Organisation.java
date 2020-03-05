package com.boclips.users.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organisation {
    private String id;
    private String contentPackageId;
    private OrganisationDetails organisationDetails;
}
