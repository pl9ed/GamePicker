plugins {
    id("java")
    kotlin("jvm")
}

group = "com.tubefans.gamepicker"

dependencies {
    implementation(project(":core:arbitragexiv"))
    api(springLibs.starterDataMongoDbReactive)
}
