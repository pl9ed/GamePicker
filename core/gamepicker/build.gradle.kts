plugins {
    kotlin("jvm")
}

group = "com.tubefans"

dependencies {
    api(discord4jLibs.discord4jCore)
    api(springLibs.starterWebflux)
    api(springLibs.jacksonModuleKotlin)

    api(libs.kotlinReflect)
    api(libs.reactorKotlinExtensions)

    api(kotlin("stdlib-jdk8"))
    testApi(springLibs.starterTest)
    testApi(libs.reactorTest)
    testApi(springLibs.mockK)
    testApi(libs.kotlinxCoroutinesTest)
}

tasks.test {
    useJUnitPlatform()
}
