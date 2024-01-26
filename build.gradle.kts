
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jlleitschuh.gradle.ktlint") version "11.3.2"
    id("jacoco")
    id("org.sonarqube") version "3.5.0.2730"
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.jpa") version "1.7.22"
}

group = "com.tubefans"
version = "0.0.1"
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
    implementation(springLibs.starterWeb)
    implementation(springLibs.jacksonModuleKotlin)

    implementation(discord4jLibs.discord4jCore)
    implementation(discord4jLibs.kotlinReflect)
    implementation(discord4jLibs.kotlinXCoroutinesReactor)
    implementation(discord4jLibs.reactorKotlinExtensions)

    implementation(googleLibs.apiClient)
    implementation(googleLibs.apiServicesSheets)
    implementation(googleLibs.drive)
    implementation(googleLibs.oauthClient)
    implementation(googleLibs.secretsManager)

    // TODO: move to library
    implementation(platform("software.amazon.awssdk:bom:2.23.11"))
    implementation("software.amazon.awssdk:aws-core")
    implementation("software.amazon.awssdk:ec2")

    developmentOnly(springLibs.devtools)
    testImplementation(libs.kotlinxCoroutinesTest)
    testImplementation(springLibs.mockK)
    testImplementation(springLibs.starterTest)
    testImplementation(discord4jLibs.reactorTest)
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
                            "**/com/tubefans/gamepicker/commands/RecommendCommand.kt"

                        )
                    }
                }
            )
        )
    }
}
