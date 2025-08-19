pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
plugins {
    id("com.highcapable.sweetdependency") version "1.0.4"
    id("com.highcapable.sweetproperty") version "1.0.8"
}
sweetProperty {
    global {
        all {
            permanentKeyValues("GITHUB_CI_COMMIT_ID" to "")
            generateFrom(ROOT_PROJECT, SYSTEM_ENV)
        }
        sourcesCode {
            includeKeys("GITHUB_CI_COMMIT_ID")
            // 关闭类型自动转换功能，防止一些特殊 "COMMIT ID" 被生成为数值
            isEnableTypeAutoConversion = false
        }
    }
    rootProject { all { isEnable = false } }
}
rootProject.name = "TSBattery"
include(":app")