// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.nordic.kotlin.kmp) apply false
    alias(libs.plugins.nordic.nexus.kmp) apply false

    // This plugin is used to generate Dokka documentation.
    alias(libs.plugins.kotlin.dokka) apply false
    // This applies Nordic look & feel to generated Dokka documentation.
    // https://github.com/NordicSemiconductor/Android-Gradle-Plugins/blob/main/plugins/src/main/kotlin/NordicDokkaPlugin.kt
    alias(libs.plugins.nordic.dokka) apply true
}

// Configure main Dokka page
dokka {
    pluginsConfiguration.html {
        homepageLink.set("https://github.com/nordicsemi/Kotlin-Util-Library")
    }
}
