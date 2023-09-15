pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
plugins {
    id("com.highcapable.sweetdependency") version "1.0.1"
    id("com.highcapable.sweetproperty") version "1.0.2"
}
sweetProperty {
    global {
        all {
            permanentKeyValues("GITHUB_CI_COMMIT_ID" to "")
            generateFrom(ROOT_PROJECT, SYSTEM_ENV)
        }
        sourcesCode { includeKeys("GITHUB_CI_COMMIT_ID") }
    }
    rootProject { all { isEnable = false } }
}
rootProject.name = "TSBattery"
include(":app")