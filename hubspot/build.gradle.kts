/*************************************************
 * build.gradle.kts
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)

    id("com.vanniktech.maven.publish") version "0.27.0"
    id("net.researchgate.release") version "3.0.2"
}

release {
    failOnUnversionedFiles = false
    git {
        requireBranch.set("release")
    }
}

tasks.named("afterReleaseBuild") {
    dependsOn("publish")
}

android {
    namespace = "com.hubspot.mobilesdk"

    buildTypes {
        release {
            buildConfigField("Boolean", "DEBUG", "false")
            buildConfigField("String", "version", "\"${version}\"")
            postprocessing {
                consumerProguardFile("consumer-rules.pro")
                isMinifyEnabled = false
            }
        }

        debug {
            buildConfigField("Boolean", "DEBUG", "true")
            buildConfigField("String", "version", "\"${version}\"")
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    implementation(libs.constraintlayout)
    implementation(libs.timber)
    implementation(libs.navigation)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.moshi)
    ksp(libs.moshi.kotlin)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.firebase.messaging)
}
