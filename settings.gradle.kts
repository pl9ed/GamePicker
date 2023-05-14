rootProject.name = "GamePicker"

dependencyResolutionManagement {
    versionCatalogs {
        create("springLibs") {
            from(files("library/spring.toml"))
        }
        create("discord4jLibs") {
            from(files("library/discord4j.toml"))
        }
        create("gdriveLibs") {
            from(files("library/gdrive.toml"))
        }
    }
}
