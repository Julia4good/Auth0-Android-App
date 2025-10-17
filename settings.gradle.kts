pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Autentication_App"
include(":app")
// Apply the custom script to extract local.properties values and inject them into the build.
// We must apply this at the settings level to ensure the properties are available to all modules
// before they are configured.
apply(from = "config.gradle.kts")

