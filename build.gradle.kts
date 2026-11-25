// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.detekt) apply false
}

subprojects {
    apply(plugin = "dev.detekt")

    extensions.configure<dev.detekt.gradle.extensions.DetektExtension> {
        config.setFrom(rootProject.file("config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
        baseline = file("detekt-baseline.xml")
    }

    // Plain "detekt"/"detektBaseline" don't use type resolution, so rules that
    // need it (e.g. SuspendFunSwallowedCancellation) only run via detektMain —
    // pull it into the standard task names CI calls.
    tasks.named("detekt") { dependsOn("detektMain") }
    tasks.named("detektBaseline") { dependsOn("detektBaselineMain") }
}