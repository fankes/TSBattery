plugins {
    autowire(libs.plugins.android.application)
    autowire(libs.plugins.kotlin.android)
    autowire(libs.plugins.kotlin.ksp)
}

android {
    namespace = property.project.app.packageName
    compileSdk = property.project.android.compileSdk

    signingConfigs {
        create("universal") {
            keyAlias = property.project.app.signing.keyAlias
            keyPassword = property.project.app.signing.keyPassword
            storeFile = rootProject.file(property.project.app.signing.storeFilePath)
            storePassword = property.project.app.signing.storePassword
            enableV1Signing = true
            enableV2Signing = true
        }
    }
    defaultConfig {
        applicationId = property.project.app.packageName
        minSdk = property.project.android.minSdk
        targetSdk = property.project.android.targetSdk
        versionName = property.project.app.versionName
        versionCode = property.project.app.versionCode
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf(
            "-Xno-param-assertions",
            "-Xno-call-assertions",
            "-Xno-receiver-assertions"
        )
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    lint { checkReleaseBuilds = false }
    androidResources.additionalParameters += listOf("--allow-reserved-package-id", "--package-id", "0x37")
}

androidComponents {
    onVariants(selector().all()) {
        it.outputs.forEach { output ->
            val currentType = it.buildType

            // Workaround for GitHub Actions.
            // Why? I don't know, but it works.
            // Unresolved reference. None of the following candidates is applicable because of receiver type mismatch:
            //                       public inline fun CharSequence.isNotBlank(): Boolean defined in kotlin.text.
            @Suppress("UNNECESSARY_SAFE_CALL", "RemoveRedundantCallsOfConversionMethods")
            val currentSuffix = property.github.ci.commit.id?.let { suffix ->
                // Workaround for GitHub Actions.
                // Strongly transfer type to [String].
                val sSuffix = suffix.toString()
                if (sSuffix.isNotBlank()) "-$sSuffix" else ""
            }
            val currentVersion = "${output.versionName.get()}$currentSuffix(${output.versionCode.get()})"
            if (output is com.android.build.api.variant.impl.VariantOutputImpl)
                output.outputFileName.set("${property.project.name}-v$currentVersion-$currentType.apk")
        }
    }
}

dependencies {
    compileOnly(de.robv.android.xposed.api)
    implementation(com.highcapable.yukihookapi.api)
    ksp(com.highcapable.yukihookapi.ksp.xposed)
    ksp(com.highcapable.hikage.hikage.compiler)
    implementation(com.highcapable.hikage.hikage.core)
    implementation(com.highcapable.hikage.hikage.extension)
    implementation(com.highcapable.hikage.hikage.widget.androidx)
    implementation(com.highcapable.hikage.hikage.widget.material)
    implementation(com.fankes.projectpromote.project.promote)
    implementation(org.luckypray.dexkit)
    implementation(com.github.duanhong169.drawabletoolbox)
    implementation(com.squareup.okhttp3.okhttp)
    implementation(androidx.core.core.ktx)
    implementation(androidx.appcompat.appcompat)
    implementation(com.google.android.material.material)
    implementation(androidx.constraintlayout.constraintlayout)
    testImplementation(junit.junit)
    androidTestImplementation(androidx.test.ext.junit)
    androidTestImplementation(androidx.test.espresso.espresso.core)
}