plugins {
    id("java")
    kotlin("jvm")
}

group = "com.tubefans"

dependencies {
    implementation(project(":core:gamepicker"))
    implementation(project(":persistence:ports"))

    api(googleLibs.apiClient)
    api(googleLibs.apiServicesSheets)
    api(googleLibs.drive)
}
