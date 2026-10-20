plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.hikage)
}

android {
    namespace = gropify.project.app.packageName
    compileSdk = gropify.project.android.compileSdk

    signingConfigs {
        create("universal") {
            keyAlias = gropify.project.app.signing.keyAlias
            keyPassword = gropify.project.app.signing.keyPassword
            storeFile = rootProject.file(gropify.project.app.signing.storeFilePath)
            storePassword = gropify.project.app.signing.storePassword
            enableV1Signing = true
            enableV2Signing = true
        }
    }
    defaultConfig {
        applicationId = gropify.project.app.packageName
        minSdk = gropify.project.android.minSdk
        targetSdk = gropify.project.android.targetSdk
        versionName = gropify.project.app.versionName
        versionCode = gropify.project.app.versionCode
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    flavorDimensions += "runtime"
    productFlavors {
        create("modern") {
            dimension = "runtime"
            isDefault = true
        }
        create("legacy") {
            dimension = "runtime"
            minSdk = gropify.project.android.legacy.minSdk
        }
    }
    buildTypes {
        all { signingConfig = signingConfigs.getByName("universal") }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    lint { checkReleaseBuilds = false }
    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }
    androidResources.additionalParameters += listOf("--allow-reserved-package-id", "--package-id", "0x37")
}

androidComponents {
    onVariants(selector().all()) {
        it.outputs.forEach { output ->
            val currentFlavor = it.flavorName
            val currentType = it.buildType
            val currentSuffix = gropify.github.ci.commit.id.let { suffix ->
                if (suffix.isNotBlank()) "-$suffix" else ""
            }
            val currentVersion = "${output.versionName.get()}$currentSuffix(${output.versionCode.get()})"
            if (output is com.android.build.api.variant.impl.VariantOutputImpl)
                output.outputFileName.set("${gropify.project.name}-v$currentVersion-$currentFlavor-$currentType.apk")
        }
    }
}

dependencies {
    platform(libs.yukihook.bom).apply {
        implementation(this)
        ksp(this)
    }
    implementation(libs.yukihook.core)
    ksp(libs.yukihook.compiler)
    implementation(libs.yukihook.extension.kavaref)
    "modernImplementation"(libs.yukihook.runtime.libxposed)
    "modernCompileOnly"(libs.libxposed.api)
    "legacyImplementation"(libs.yukihook.runtime.xposed82)
    "legacyCompileOnly"(libs.xposed.framework.api)
    implementation(platform(libs.kavaref.bom))
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.android)
    implementation(libs.kavaref.extension)
    implementation(platform(libs.hikage.bom))
    implementation(libs.hikage.core)
    implementation(libs.hikage.runtime)
    implementation(libs.hikage.runtime.attribute)
    implementation(libs.hikage.extension)
    implementation(libs.hikage.widget.androidx)
    implementation(libs.hikage.widget.material)
    implementation(libs.project.promote)
    implementation(libs.dexkit)
    implementation(libs.drawabletoolbox)
    implementation(libs.okhttp)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}