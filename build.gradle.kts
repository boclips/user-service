import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.2.0.RELEASE"
	id("io.spring.dependency-management") version "1.0.8.RELEASE"
	kotlin("jvm") version "1.3.50"
	kotlin("plugin.spring") version "1.3.50"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
	maven("https://jitpack.io")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}

	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.security.oauth:spring-security-oauth2:2.1.4.RELEASE")
	
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-hateoas")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.retry:spring-retry")
	implementation("org.springframework.boot:spring-boot-starter-aop")

	implementation("com.github.boclips:event-bus:1.37.0")
	implementation("com.github.boclips:boclips-spring-web:0.128.0")
	implementation("com.github.boclips:boclips-spring-security:0.190.0")
	implementation("com.github.boclips:videos:0.0.619")

	implementation("com.jayway.jsonpath:json-path:2.3.0")
	implementation("io.github.microutils:kotlin-logging:1.6.22")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.keycloak:keycloak-admin-client:4.8.3.Final")
	implementation("org.jboss.resteasy:resteasy-jackson2-provider:3.6.2.Final")
	implementation("org.jboss.resteasy:resteasy-client:3.6.2.Final")
	implementation("org.keycloak:keycloak-authz-client:4.8.3.Final")
	implementation("com.mixpanel:mixpanel-java:1.4.4")
	implementation("com.google.guava:guava:27.0.1-jre")
	implementation("commons-validator:commons-validator:1.6")
	implementation("commons-beanutils:commons-beanutils:1.9.4")
	implementation("io.sentry:sentry-logback:1.7.21")
	implementation("io.micrometer:micrometer-core")
	implementation("io.micrometer:micrometer-registry-prometheus")
	implementation("io.opentracing.contrib:opentracing-spring-jaeger-web-starter:2.0.2")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.assertj:assertj-core:3.11.1")
	testImplementation("org.yaml:snakeyaml:1.23")
	testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
	testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.0.0")
	testImplementation("org.awaitility:awaitility:3.1.0")
	testImplementation("org.springframework.cloud:spring-cloud-contract-wiremock:2.1.0.RELEASE")

}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}
