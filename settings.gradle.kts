
@Suppress("UnstableApiUsage")
pluginManagement {
    repositories {
        mavenLocal()
        google {
            content {
                includeGroupAndSubgroups("com.google")
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
            }
        }
        mavenCentral()
    }
    versionCatalogs {
        // Use Nordic Gradle Version Catalog with common external libraries versions.
        create("libs") {
            from("no.nordicsemi.android.gradle:version-catalog:local")
        }
    }
}

rootProject.name = "Kotlin Util Library"

include(":data")
 