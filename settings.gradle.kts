import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

pluginManagement {
    fun getMavenCredentials(): Pair<String, String>? {
        val userHome = System.getProperty("user.home")
        val mavenSettingsFile = File(userHome, ".m2/settings.xml")
        if (!mavenSettingsFile.exists()) {
            return null
        }
        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document = documentBuilder.parse(mavenSettingsFile)
        val servers = document.getElementsByTagName("server")
        for (i in 0 until servers.length) {
            val serverNode = servers.item(i)
            val children = serverNode.childNodes
            var id: String? = null
            var username: String? = null
            var password: String? = null
            for (j in 0 until children.length) {
                val node = children.item(j)
                when (node.nodeName) {
                    "id" -> id = node.textContent
                    "username" -> username = node.textContent
                    "password" -> password = node.textContent
                }
            }
            if (id == "HubSpot-Nexus" && username != null && password != null) {
                return Pair(username, password)
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
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    fun getMavenCredentials(): Pair<String, String>? {
        val userHome = System.getProperty("user.home")
        val mavenSettingsFile = File(userHome, ".m2/settings.xml")
        if (!mavenSettingsFile.exists()) {
            return null
        }
        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document = documentBuilder.parse(mavenSettingsFile)
        val servers = document.getElementsByTagName("server")
        for (i in 0 until servers.length) {
            val serverNode = servers.item(i)
            val children = serverNode.childNodes
            var id: String? = null
            var username: String? = null
            var password: String? = null
            for (j in 0 until children.length) {
                val node = children.item(j)
                when (node.nodeName) {
                    "id" -> id = node.textContent
                    "username" -> username = node.textContent
                    "password" -> password = node.textContent
                }
            }
            if (id == "HubSpot-Nexus" && username != null && password != null) {
                return Pair(username, password)
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
    }
}
rootProject.name = "mobile-chat-sdk-android"
include(":demo")
include(":hubspot")
