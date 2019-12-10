package com.boclips.users.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String organisationAccountId;
    private List<Subject> subjects;
    private TeacherPlatformAttributes teacherPlatformAttributes;
}
