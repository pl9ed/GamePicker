plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "GamePicker"

dependencyResolutionManagement {
    versionCatalogs {
        create("springLibs") {
            from(files("library/spring.toml"))
        }
        create("discord4jLibs") {
            from(files("library/discord4j.toml"))
        }
        create("googleLibs") {
            from(files("library/google.toml"))
        }
        create("libs") {
            from(files("library/general.toml"))
        }
    }
}

include("spring-application")
include("core")
include("arbitragexiv")
