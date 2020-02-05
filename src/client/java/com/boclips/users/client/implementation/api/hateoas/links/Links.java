package com.boclips.users.client.implementation.api.hateoas.links;

import lombok.Data;

@Data
public class Links {
    private Link user;
    private Link contracts;
    private Link account;
    private Link validateShareCode;
}
