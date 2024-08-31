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
}
