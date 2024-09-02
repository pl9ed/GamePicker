plugins {
    id("java")
    kotlin("jvm")
}

group = "com.tubefans"

dependencies {
    implementation(project(":core:arbitragexiv"))
    implementation(project(":aws"))
    implementation(project(":persistence:mongo-persistence"))
    implementation(googleLibs.secretsManager)
    implementation(libs.kotlinReflect)
}
