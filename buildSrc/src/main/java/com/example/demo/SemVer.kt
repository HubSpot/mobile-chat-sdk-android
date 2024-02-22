package com.example.demo

import java.util.Properties
import org.gradle.api.Project
import org.slf4j.LoggerFactory

/**
 * Calculate the project version name and code base on the format:
 *
 * versionName : "major.minor.build"
 * versionCode: An incrementing Integer
 *
 * @version 1.0
 */
object SemVer {
    private val major = validate(1, "Major")
    private val minor = validate(0, "Minor")
    private val Project.build: Int
        get() = validate(getTeamCityBuild() ?: 0, "Build")

    @JvmStatic
    fun name(project: Project): String {
        val versionName = "$major.$minor.${project.build}"

        return if (project.hasProperty("teamcity")) { // Need to print this so teamcity can set it as its current build number.
            println("##teamcity[buildNumber '$versionName']")
            versionName
        } else {
            versionName
        }.removeSuffix(".0") // See: https://bugs.tapadoo.com/issue/PPPAND-687 -> Exclude patch/build number if it's zero
    }

    /**
     * Calculate the versionCode to return to the project. Each field (major, minor, build) will be
     * padded to 3 digits. Each field should not exceed this limit.
     *
     * Examples:
     * major -> 999
     * minor -> 888
     * build -> 777
     * code = 999888777
     *
     * major -> 9
     * minor -> 8
     * build -> 7
     * code = 9008007
     *
     * NOTE: Version code cannot be larger that 2_100_000_000.
     * see: https://developer.android.com/studio/publish/versioning.html
     */
    @JvmStatic
    fun code(project: Project): Int = (major * 1_000_000 + minor * 1_000 + project.build).takeIf { it < 2_100_000_000 }
            ?: throw Exception("Version code is too large, this will be rejected by the Play Store.")


    /**
     * The TeamCity build number can be in two formats depending on what stage of the build process
     * this function gets called and how many times before it was called.
     *
     * Build number set by TeamCity: This will be the initial value of the build number on a fresh
     * build. It will be a single Integer.
     *
     * Build number set by us after being formatted: This will be the formatted version of the build
     * number (e.g. 1.2.3)
     */
    private fun Project.getTeamCityBuild(): Int? = if (project.hasProperty("teamcity")) {
        ((project.property("teamcity") as Properties)["build.number"] as String).split(".").last().toInt()
    } else {
        null
    }

    private fun validate(input: Int, name: String): Int {
        if (input < 1000) return input

        LoggerFactory.getLogger("SemVer").error("SemVer: $name number too large, must be lower than 1000")
        throw Exception() // Throw an empty exception so the build is stopped.
    }
}