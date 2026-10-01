plugins {
    `kotlin-dsl`
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
    mavenCentral()
}