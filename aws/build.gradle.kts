plugins {
    id("java")
    kotlin("jvm") version "2.0.20"
}

group = "com.tubefans.gamepicker"

dependencies {
    implementation(project(":core:gamepicker"))

    api(platform("software.amazon.awssdk:bom:2.23.11"))
    api("software.amazon.awssdk:aws-core")
    api("software.amazon.awssdk:ec2")
}
