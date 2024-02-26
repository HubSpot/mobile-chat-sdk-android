import com.example.demo.SemVer

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.detekt)
    alias(libs.plugins.safe.args)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.demo"

    defaultConfig {
        applicationId = "com.example.demo"
        versionCode = SemVer.code(project)
        versionName = SemVer.name(project)
        base.archivesName.set("hubspot-demo-$versionName")
    }

    buildTypes {
        release {
            postprocessing {
                proguardFile("proguard-rules.pro")
                isRemoveUnusedCode = true
                isRemoveUnusedResources = true
                isOptimizeCode = true
                isObfuscate = true
            }
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":hubspot"))

    ksp(libs.hilt.android.compiler)

    implementation(libs.androidx.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    implementation(libs.constraintlayout)
    implementation(libs.timber)
    implementation(libs.navigation)
    implementation(libs.navigation.ktx)
    implementation(libs.datastore)
    implementation(libs.hilt.android)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.process)
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.moshi)
    ksp(libs.moshi.kotlin)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.firebase.messaging)
}