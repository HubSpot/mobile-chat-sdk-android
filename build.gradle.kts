/*************************************************
 * build.gradle.kts
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.google.services)
}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath(libs.dokka.base)
    }
}
subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_17.toString()
            freeCompilerArgs += listOf("-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi", "-Xstring-concat=inline")
        }
    }

    tasks.withType<DokkaTask>().configureEach {
        suppressInheritedMembers = true
        moduleName.set(project.name)
        moduleVersion.set(project.version.toString())
        failOnWarning.set(false)
        suppressObviousFunctions.set(true)
        offlineMode.set(false)
        outputDirectory.set(rootDir.resolve("docs"))
        dokkaSourceSets {
            moduleName.set("HubspotMobile SDK")
            configureEach {
                includes.from(project.files(), "HubspotMobileSDK.md")
            }

        }
        pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
            customStyleSheets = listOf(file("logo-styles.css"))
            customAssets = listOf(file("hubspot-logo.png"))
            footerMessage = "<br><div>Hubspot Mobile SDK</div><br><div>Copyright © 2024 Hubspot, Inc.</div><br>"
            separateInheritedMembers = false
            mergeImplicitExpectActualDeclarations = false
        }
    }

    afterEvaluate {
        extensions.findByType<com.android.build.gradle.BaseExtension>()?.run {
            compileSdkVersion(34)
            defaultConfig {
                minSdk = 26
                targetSdk = 34
            }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }
    }
}

allprojects {
    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        reports {
            xml.required.set(false)
            txt.required.set(false)
            sarif.required.set(false)
            html.required.set(true)
            html.outputLocation.set(file("${project.buildDir}/reports/detekt/detekt.html"))
        }
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "8.4"
    distributionType = Wrapper.DistributionType.BIN
}
