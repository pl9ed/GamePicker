plugins {
    id("java")
    kotlin("jvm") version "2.0.20"
}

group = "com.tubefans.gamepicker"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":persistence:ports"))

    api("com.google.api-client:google-api-client:1.25.0")
    api("com.google.apis:google-api-services-sheets:v4-rev612-1.25.0")
    api("com.google.apis:google-api-services-drive:v3-rev197-1.25.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
