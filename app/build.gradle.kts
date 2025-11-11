import org.gradle.kotlin.dsl.implementation
import org.gradle.kotlin.dsl.ksp

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    //hilt
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
//    google-services
    id("com.google.gms.google-services")
}

android {
    namespace = "com.sam.ayaana"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.sam.ayaana"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

}


dependencies {

    //hilt
    implementation(libs.hilt.android)
    implementation(libs.generativeai)
    ksp(libs.hilt.android.compiler)

    //navigation
    implementation(libs.androidx.navigation.compose)

    //hilt navigation
    implementation(libs.androidx.hilt.navigation.compose)

    //retrofit dep
    implementation(libs.converter.moshi)
    implementation(libs.retrofit)

    //datastore
    implementation(libs.androidx.datastore.preferences)

    //FirebaseBom
    implementation(platform(libs.firebase.bom)) // Version from your dependencies

    //Coil
    implementation(libs.coil.compose)

    //Firebase Authentication
    implementation(libs.firebase.auth.ktx)

    //Google Sign-In
    implementation(libs.play.services.auth)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.paging)

    // Retrofit & Networking with Moshi
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.okhttp.logging.interceptor)
    ksp(libs.moshi.kotlin.codegen)
    // ksp(libs.moshi.kotlin.codegen.ksp)

    //pagination
    implementation (libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    //material-icons
    implementation(libs.androidx.compose.material.icons.extended)

    //    implementation(libs.firebase.auth)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

configurations.all {
    resolutionStrategy {
        force(
            "com.squareup.moshi:moshi:1.15.1",
            "com.squareup.moshi:moshi-kotlin:1.15.1"
        )
    }
}