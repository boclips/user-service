package com.boclips.users.client.model.contract;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SelectedContentContract.class, name = "SelectedContent")
})
@Data
public abstract class Contract {
    protected String id;
    protected String name;
}