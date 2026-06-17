/*************************************************
 * build.gradle.kts
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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
    fun getMavenCredentials(): Pair<String, String>? {
        val mavenSettingsFile = File(System.getProperty("user.home"), ".m2/settings.xml")
        if (!mavenSettingsFile.exists()) return null
        val content = mavenSettingsFile.readText()
        val serverBlocks = Regex("""<server>(.*?)</server>""", RegexOption.DOT_MATCHES_ALL).findAll(content)
        for (server in serverBlocks) {
            val block = server.groupValues[1]
            val idMatch = Regex("""<id>\s*([^<]+)\s*</id>""").find(block)
            if (idMatch != null && idMatch.groupValues[1].trim() == "HubSpot-Nexus") {
                val usernameMatch = Regex("""<username>\s*([^<]+)\s*</username>""").find(block)
                val passwordMatch = Regex("""<password>\s*([^<]+)\s*</password>""").find(block)
                if (usernameMatch != null && passwordMatch != null) {
                    return Pair(usernameMatch.groupValues[1].trim(), passwordMatch.groupValues[1].trim())
                }
            }
        }
        return null
    }

    repositories {
        if (System.getenv("BLAZAR_COORDINATES") != null) {
            maven("https://nexus.hubteam.com/nexus-maven/repository/hubspot-development/") {
                credentials {
                    val creds = getMavenCredentials()
                    if (creds != null) {
                        username = creds.first
                        password = creds.second
                    }
                }
            }
        }
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
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.addAll(
                "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-Xstring-concat=inline"
            )
        }
    }

    tasks.withType<DokkaTask>().configureEach {
        suppressInheritedMembers = true
        moduleName.set(project.name)
        moduleVersion.set(project.version.toString())
        failOnWarning.set(false)
        suppressObviousFunctions.set(true)
        offlineMode.set(false)
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
            compileSdkVersion(36)
            buildToolsVersion = "36.0.0"
            defaultConfig {
                minSdk = 26
                targetSdk = 35
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
    gradleVersion = "8.13"
    distributionType = Wrapper.DistributionType.BIN
}
