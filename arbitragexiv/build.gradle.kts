plugins {
    id("java")
    kotlin("jvm") version "2.0.20"
}

group = "com.tubefans"

dependencies {
    api(project(":core:arbitragexiv"))
    implementation(project(":aws"))
    implementation(project(":persistence:mongo-persistence"))
    implementation(googleLibs.secretsManager)
    implementation(libs.kotlinReflect)

    testImplementation(springLibs.starterTest)
    testImplementation(libs.reactorTest)
}
