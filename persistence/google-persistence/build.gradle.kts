plugins {
    id("java")
    kotlin("jvm")
}

group = "com.tubefans.gamepicker"

dependencies {
    implementation(project(":core"))
    implementation(project(":persistence:ports"))

    api(googleLibs.apiClient)
    api(googleLibs.apiServicesSheets)
    api(googleLibs.drive)
}
