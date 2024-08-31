plugins {
    id("java")
    kotlin("jvm")
}

group = "com.tubefans.gamepicker"

dependencies {
    implementation(project(":core"))
    api(springLibs.starterDataMongoDbReactive)
}
