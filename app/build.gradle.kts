plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.spwallet"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mrkefish.spwallet"
        minSdk = 28
        targetSdk = 35
        versionCode = 3
        versionName = "0.1.2"
        applicationId = "com.mrkefish.spwallet"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.okhttp)
    implementation(libs.preference)
    implementation(libs.gson)
    implementation(libs.picasso)
    implementation(libs.webkit)
    implementation(libs.material3)
    implementation(libs.retrofit)
    implementation(libs.retrofit2.converter.gson)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}