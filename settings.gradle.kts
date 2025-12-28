rootProject.name = "EndSectors"
include("common")
include("Tools")
include("paper")
include("proxy")
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    
    }
}
