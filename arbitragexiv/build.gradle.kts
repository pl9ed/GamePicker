plugins {
    id("java")
    kotlin("jvm")
}

group = "com.tubefans.gamepicker"

dependencies {
    implementation(project(":core"))
    implementation(project(":aws"))
    implementation(project(":persistence:mongo-persistence"))
    implementation(googleLibs.secretsManager)
    implementation(libs.kotlinReflect)
}
