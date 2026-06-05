pluginManagement {
    repositories {
        if (System.getenv("BLAZAR_COORDINATES") != null) {
            maven("https://nexus.hubspot.com/nexus-maven/repository/hubspot-development/")
        }
        google()
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

rootProject.name = "mobile-chat-sdk-android"
include(":demo")
include(":hubspot")
