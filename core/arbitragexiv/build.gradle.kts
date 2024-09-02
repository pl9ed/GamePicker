plugins {
    kotlin("jvm")
}

group = "com.tubefans.arbitragexiv"

dependencies {
    api(libs.kotlinXCoroutinesReactor)
    api(libs.reactorKotlinExtensions)
    api(springLibs.starterWebflux)
    api(springLibs.jacksonModuleKotlin)

    testApi(springLibs.starterTest)
    testApi(libs.reactorTest)
    testApi(springLibs.mockK)
    testApi(libs.kotlinxCoroutinesTest)
}