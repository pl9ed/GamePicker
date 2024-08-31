plugins {
    kotlin("jvm")
}

group = "com.tubefans.gamepicker"

dependencies {
    api(discord4jLibs.discord4jCore)
    api(springLibs.starterWebflux)
    api(springLibs.jacksonModuleKotlin)

    api(libs.kotlinReflect)
    api(libs.kotlinxCoroutinesTest)
    api(libs.reactorKotlinExtensions)

    api(kotlin("stdlib-jdk8"))
    testApi(springLibs.starterTest)
    testApi(libs.reactorTest)
    testApi(springLibs.mockK)
}

tasks.test {
    useJUnitPlatform()
}
