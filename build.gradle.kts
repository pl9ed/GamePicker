plugins {
    id("java")
    kotlin("jvm") version "2.0.20" apply false
    id("jacoco")
    id("org.sonarqube") version "5.0.0.4638"
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "jacoco")
    apply(plugin = "jacoco-report-aggregation")
    apply(plugin = "org.sonarqube")

    repositories {
        mavenCentral()
    }

    extra["rootDirPath"] = rootProject.projectDir.absolutePath

    tasks.withType<Test> {
        useJUnitPlatform()
        systemProperty("rootDirPath", rootProject.projectDir.absolutePath)
        filter {
            excludeTestsMatching("*.integration.*")
        }
    }

    tasks.withType<JavaExec> {
        systemProperty("rootDirPath", rootProject.projectDir.absolutePath)
    }

    tasks.check {
        dependsOn(tasks.named<JacocoReport>("jacocoTestReport"))
    }

    tasks.jacocoTestReport {
        dependsOn("test")
        reports {
            xml.required.set(true)
        }
    }
}

sonar {
    properties {
        property("sonar.projectKey", "pl9ed_game-picker")
        property("sonar.organization", "pl9ed")
        property("sonar.host.url", "https://sonarcloud.io")

        val excludedPackages =
            listOf(
                "**/GamePickerApplication.kt",
                "**/CommandListener.kt",
                "**/com/tubefans/**/models/**/*",
                "**/com/tubefans/**/config/**/*",
                "**/GlobalCommandRegistrar.kt",
                "**/com/tubefans/**/repositories/**/*",
                "**/com/tubefans/**/exceptions/**/*",
                "**/com/tubefans/gamepicker/listeners/**/*",
            )

        property("sonar.coverage.exclusions", excludedPackages)
    }
}
