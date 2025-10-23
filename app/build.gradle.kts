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
        minSdk = 34
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
//    implementation(platform(libs.firebase.bom.v3274)) // Replace with the latest BoM version

    //Firebase Authentication
    implementation(libs.google.firebase.auth.ktx)

//    implementation(libs.firebase.authentication) // Version managed by BoM

//    firebase-auth
    implementation(libs.google.firebase.auth)
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