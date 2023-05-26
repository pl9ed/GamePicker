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
