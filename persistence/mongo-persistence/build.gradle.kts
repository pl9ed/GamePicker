plugins {
    id("java")
    kotlin("jvm")
}

group = "com.tubefans"

dependencies {
    implementation(project(":core:arbitragexiv"))
    api(springLibs.starterDataMongoDbReactive)
}
