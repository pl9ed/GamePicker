import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("jacoco")
    id("org.sonarqube") version "5.0.0.4638"
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.jpa") version "1.7.22"
}

group = "com.tubefans"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_17

sonarqube {
    properties {
        property("sonar.projectKey", "pl9ed_game-picker")
        property("sonar.organization", "pl9ed")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":persistence:ports"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    implementation("com.google.api-client:google-api-client:1.25.0")
    implementation("com.google.apis:google-api-services-sheets:v4-rev612-1.25.0")
    implementation("com.google.apis:google-api-services-drive:v3-rev197-1.25.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.cloud:google-cloud-secretmanager:2.17.0")

    implementation(platform("software.amazon.awssdk:bom:2.23.11"))
    implementation("software.amazon.awssdk:aws-core")
    implementation("software.amazon.awssdk:ec2")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-inline:3.11.2")
    testImplementation("io.projectreactor:reactor-test:3.6.8")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    filter {
        excludeTestsMatching("*.integration.*")
    }
}

tasks.jacocoTestReport {
    dependsOn("test")
    reports {
        xml.required.set(true)
    }
}

tasks.withType<JacocoReport> {
    afterEvaluate {
        classDirectories.setFrom(
            files(
                classDirectories.files.map {
                    fileTree(it).apply {
                        exclude(
                            "**/com/tubefans/gamepicker/repositories/**/*",
                            "**/com/tubefans/gamepicker/commands/RecommendCommand.kt",
                        )
                    }
                },
            ),
        )
    }
}
