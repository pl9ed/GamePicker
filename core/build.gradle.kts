plugins {
    id("java")
    kotlin("jvm") version "2.0.20"
}

group = "com.tubefans.gamepicker"

repositories {
    mavenCentral()
}

dependencies {
    api("com.discord4j:discord4j-core:3.2.4")
    api("org.springframework.boot:spring-boot-starter-web")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")

    api("org.jetbrains.kotlin:kotlin-reflect")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    api("io.projectreactor.kotlin:reactor-kotlin-extensions")

    api(kotlin("stdlib-jdk8"))
    testApi("org.springframework.boot:spring-boot-starter-test")
    testApi("io.projectreactor:reactor-test:3.6.8")
    testApi(platform("org.junit:junit-bom:5.10.0"))
    testApi("org.junit.jupiter:junit-jupiter")
    testApi("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testApi("io.mockk:mockk:1.13.5")
    testApi("org.mockito:mockito-inline:3.11.2")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
