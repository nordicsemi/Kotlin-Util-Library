// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // This plugin is added here only for readability.
    // It is automatically applied by the `libs.plugins.nordic.publish.kmp` plugin below.
    alias(libs.plugins.kotlin.multiplatform) apply false
    // This plugins configures Android KMP mocule.
    alias(libs.plugins.nordic.android.kmp.library) apply false
    // Set the Kotlin version and JVM toolchain in one place for all modules, including KMP modules.
    alias(libs.plugins.nordic.kotlin) apply false
    // Required for publishing KMP modules on Maven Central.
    alias(libs.plugins.nordic.publish.kmp) apply false
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
