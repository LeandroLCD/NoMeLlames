@file:Suppress("UnstableApiUsage")
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.kotlin.serialization)
}

val localSigningProperties: Properties = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

fun signingProp(name: String): String? =
    localSigningProperties.getProperty(name)?.takeIf { it.isNotBlank() }

android {
    namespace = "cl.blipblipcode.prefixsapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "cl.blipblipcode.prefixsapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 6
        versionName = "1.2.01"

        testInstrumentationRunner = "cl.blipblipcode.prefixsapp.HiltTestRunner"
    }

    signingConfigs {
        create("release") {
            val keystoreFilePath = providers.environmentVariable("KEYSTORE_FILE").orNull
                ?: signingProp("prefixsapp.signing.storeFile")

            if (!keystoreFilePath.isNullOrBlank()) {
                storeFile = rootProject.file(keystoreFilePath)
                storePassword = providers.environmentVariable("KEYSTORE_PASSWORD").orNull
                    ?: signingProp("prefixsapp.signing.storePassword")
                    ?: ""
                keyAlias = providers.environmentVariable("KEY_ALIAS").orNull
                    ?: signingProp("prefixsapp.signing.keyAlias")
                    ?: ""
                keyPassword = providers.environmentVariable("KEY_PASSWORD").orNull
                    ?: signingProp("prefixsapp.signing.keyPassword")
                    ?: ""
            }
        }
    }

    buildTypes {
        val isSigningConfigured = providers.environmentVariable("KEYSTORE_FILE").isPresent ||
                                 signingProp("prefixsapp.signing.storeFile") != null

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            ndk {
                debugSymbolLevel = "SYMBOL_TABLE"
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders += mapOf(
                "buildserver" to "release",
            )
            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                nativeSymbolUploadEnabled = true
            }

            signingConfig = if (isSigningConfigured) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
        }
        create("apk"){
            applicationIdSuffix = ".apk"
            manifestPlaceholders += mapOf(
                "buildserver" to "apk",
            )
            signingConfig = if (isSigningConfigured) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
        }
        debug {
            applicationIdSuffix = ".debug"
            manifestPlaceholders += mapOf(
                "buildserver" to "debug",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    testFixtures {
        enable = true
        androidResources = true
    }
}

kotlin{
    compilerOptions{
        jvmTarget = JvmTarget.JVM_17
        freeCompilerArgs = listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.biometric.ktx)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.config)
    implementation(libs.bundles.navigation3)
    implementation(libs.kotlinx.serialization.json)
    ksp(libs.androidx.room.compiler)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.timber)
    implementation(libs.androidx.datastore.preferences)
    implementation(project(":specialbottombar"))
    ksp(libs.hilt.compiler)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(testFixtures(project(":app")))
    androidTestImplementation(testFixtures(project(":app")))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.turbine)
    kspAndroidTest(libs.hilt.compiler)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Test Fixtures
    testFixturesImplementation(platform(libs.androidx.compose.bom))
    testFixturesImplementation(libs.androidx.compose.ui)
    testFixturesImplementation(libs.androidx.compose.runtime)
    testFixturesImplementation(libs.junit)
    testFixturesImplementation(libs.kotlinx.coroutines.test)
    testFixturesImplementation(libs.turbine)
}

tasks.register("testUnitApp") {
    group = "verification"
    description = "Runs all unit tests for the :app module (JVM, src/test/) across every available variant."
    dependsOn("test")
}

tasks.register("androidTestApp") {
    group = "verification"
    description = "Runs all instrumented androidTest for the :app module (src/androidTest/) on every available connected variant."
    dependsOn("connectedAndroidTest")
}
