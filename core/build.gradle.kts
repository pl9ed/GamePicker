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

    implementation(kotlin("stdlib-jdk8"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
