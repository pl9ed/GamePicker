import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jetbrains.kotlin.plugin.spring") version "2.0.20"
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.allopen") version "2.0.20"
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

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":core:gamepicker"))
    implementation(project(":persistence:ports"))
    implementation(project(":persistence:mongo-persistence"))
    implementation(project(":persistence:google-persistence"))
    implementation(project(":aws"))
    implementation(project(":arbitragexiv"))

    implementation(googleLibs.oauthClient)
    implementation(googleLibs.secretsManager)
    implementation(libs.kotlinXCoroutinesReactor)

    developmentOnly("org.springframework.boot:spring-boot-devtools")

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
