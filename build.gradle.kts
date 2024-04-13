// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.2" apply false
}
/*
allprojects{
    repositories {
        google()
        mavenCentral()
        // Mapbox Maven repository
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            // Do not change the username below. It should always be "mapbox" (not your username).
            credentials.username = "mapbox"
            // Use the secret token stored in gradle.properties as the password
            credentials.password = providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN").get()
            authentication.create<BasicAuthentication>("basic")
        }
    }
}*/
