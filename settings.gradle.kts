
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        maven {
            url = uri("https://repo.maven.apache.org/maven2")
        }
    }
}

rootProject.name = "NovaOS"
include(":app")
