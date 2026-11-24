import java.io.File

pluginManagement {
    fun getMavenCredentials(): Pair<String, String>? {
        val mavenSettingsFile = File(System.getProperty("user.home"), ".m2/settings.xml")
        if (!mavenSettingsFile.exists()) return null

        val content = mavenSettingsFile.readText()
        val serverBlocks = Regex(
            """<server>(.*?)</server>""",
            RegexOption.DOT_MATCHES_ALL
        ).findAll(content)

        for (server in serverBlocks) {
            val block = server.groupValues[1]
            val idMatch = Regex("""<id>\s*([^<]+)\s*</id>""").find(block)

            if (idMatch != null && idMatch.groupValues[1].trim() == "HubSpot-Nexus") {
                val usernameMatch = Regex("""<username>\s*([^<]+)\s*</username>""").find(block)
                val passwordMatch = Regex("""<password>\s*([^<]+)\s*</password>""").find(block)

                if (usernameMatch != null && passwordMatch != null) {
                    return Pair(
                      usernameMatch.groupValues[1].trim(),
                      passwordMatch.groupValues[1].trim()
                    )
                }
            }
        }

        return null
    }

    repositories {
        if (System.getenv("BLAZAR_COORDINATES") != null) {
            maven("https://nexus.hubteam.com/nexus-maven/repository/hubspot-development/") {
                credentials {
                    getMavenCredentials()?.let { (usernameValue, passwordValue) ->
                        username = usernameValue
                        password = passwordValue
                    }
                }
            }
        } else {
            // Keep public builds usable outside HubSpot/Blazar.
            google()
            mavenCentral()
            gradlePluginPortal()
        }
    }
}