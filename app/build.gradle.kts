import com.android.build.api.variant.BuildConfigField
import java.io.ByteArrayOutputStream
import java.util.regex.Pattern

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.neolife.devfine"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.neolife.devfine"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.17"

        buildConfigField("String", "BUILD_TIME", "\"" + System.currentTimeMillis().toString() + "\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }


    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BUILD_TIME", "\"" + System.currentTimeMillis().toString() + "\"")
            signingConfig = signingConfigs.getByName("debug")
        }

        debug {
            isMinifyEnabled = false
            buildConfigField("String", "BUILD_TIME", "\"" + System.currentTimeMillis().toString() + "\"")
            isShrinkResources = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


dependencies {
    implementation(libs.markdown) {
        this.exclude("com.atlassian.commonmark")
    }
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.jetbrains.markdown)
    implementation("com.halilibo.compose-richtext:richtext-commonmark-android:1.0.0-alpha01")
    implementation("com.halilibo.compose-richtext:richtext-markdown-android:1.0.0-alpha01")
    implementation("com.halilibo.compose-richtext:richtext-ui-material3-android:1.0.0-alpha01")
    implementation(libs.piashsarker.androidAppUpdateLibrary)
    implementation(libs.coil.kt)
    implementation(libs.android.core.splashscreen)
    implementation(libs.koin.android)
    implementation(libs.koin.core)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}