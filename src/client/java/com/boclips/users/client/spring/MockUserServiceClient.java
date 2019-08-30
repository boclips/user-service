package com.boclips.users.client.spring;

import com.boclips.users.client.implementation.FakeUserServiceClient;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(FakeUserServiceClient.class)
public @interface MockUserServiceClient {
}
