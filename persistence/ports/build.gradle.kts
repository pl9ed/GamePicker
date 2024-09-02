plugins {
    id("java")
    kotlin("jvm") version "2.0.20"
}

group = "com.tubefans"

dependencies {
    implementation(project(":core:gamepicker"))
}
