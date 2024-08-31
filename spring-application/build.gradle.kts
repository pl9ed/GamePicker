import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("jacoco")
    id("org.sonarqube") version "5.0.0.4638"
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "2.0.20"
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
    implementation(project(":persistence:google-sheets-persistence"))

    implementation(googleLibs.oauthClient)
    implementation(googleLibs.secretsManager)

    implementation(platform("software.amazon.awssdk:bom:2.23.11"))
    implementation("software.amazon.awssdk:aws-core")
    implementation("software.amazon.awssdk:ec2")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation(kotlin("stdlib-jdk8"))

    testImplementation(springLibs.starterTest)
    testImplementation(libs.reactorTest)
    testImplementation(libs.kotlinxCoroutinesTest)
    testImplementation(springLibs.mockK)
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
