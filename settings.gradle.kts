
@Suppress("UnstableApiUsage")
pluginManagement {
    repositories {
        mavenLocal()
        google {
            content {
                includeGroupAndSubgroups("com.google")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("androidx")
            }
        }
        gradlePluginPortal {
            content {
                includeGroupAndSubgroups("com.gradle")
                includeGroupAndSubgroups("no.nordicsemi")
                includeGroupAndSubgroups("org.jetbrains")
            }
        }
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google {
            content {
                includeGroupAndSubgroups("com.google")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("androidx")
            }
        }
        mavenCentral()
    }
    versionCatalogs {
        // Use Nordic Gradle Version Catalog with common external libraries versions.
        create("libs") {
            from("no.nordicsemi.gradle:version-catalog-min-sdk-21:3.0")
        }
    }
}

rootProject.name = "Kotlin Util Library"

include(":data")
include(":id")
include(":log")
 