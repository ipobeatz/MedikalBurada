plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    id(libs.plugins.kotlin.kapt.get().pluginId)
    alias(libs.plugins.paparazzi)
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs.kotlin")
    kotlin(libs.plugins.plugin.serializaton.get().pluginId)
}

android {

    namespace = "com.android.medikalburada"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.android.medikalburada"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
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
        viewBinding = true
    }
    hilt {
        enableAggregatingTask = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    buildToolsVersion = "34.0.0"

}


dependencies {
    implementation(libs.bundles.androidxCore)
    implementation(libs.bundles.lifecycle)
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    kapt ("com.github.bumptech.glide:compiler:4.15.1")
    implementation(libs.bundles.navigation)
    implementation(libs.bundles.hilt)
    implementation(libs.bundles.otherLibraries)
    testImplementation (libs.kotlin.test)
    implementation(libs.bundles.retrofit)
    implementation(libs.androidx.media3.session)
    implementation(libs.firebase.auth.ktx)
    kapt(libs.bundles.hiltKapt)
    implementation ("com.google.android.flexbox:flexbox:3.0.0")
    annotationProcessor(libs.hiltCompiler)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation (libs.play.services.auth)
    testImplementation(libs.bundles.testing)
    androidTestImplementation(libs.bundles.uiTesting)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidxAppcompat)
    implementation(libs.bundles.hilt)
    androidTestImplementation(libs.bundles.testing)
    implementation (libs.okhttp)
    implementation (libs.exoplayer.v2181)
    implementation (libs.exoplayer.ui)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)
    testImplementation ("org.robolectric:robolectric:4.10")
    testImplementation ("androidx.test:core:1.5.0")
    implementation ("com.google.firebase:firebase-firestore-ktx:24.7.1")
}