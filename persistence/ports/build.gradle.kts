plugins {
    id("java")
    kotlin("jvm")
}

group = "com.tubefans.gamepicker"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
}
