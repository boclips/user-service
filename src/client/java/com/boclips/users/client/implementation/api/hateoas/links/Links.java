package com.boclips.users.client.implementation.api.hateoas.links;

import lombok.Data;

@Data
public class Links {
    private Link user;
    private Link accessRules;
    private Link organisation;
    private Link validateShareCode;
    private Link contentPackage;
}
