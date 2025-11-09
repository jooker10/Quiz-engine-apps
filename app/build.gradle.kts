plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.20"
    id("com.google.gms.google-services")

}

android {
    namespace = "futur.apps.composeproject1"
    compileSdk = 36

    defaultConfig {
        applicationId = "futur.apps.composeproject1"
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
    }
    kapt{
        correctErrorTypes = true
    }
}


dependencies {

    // --- Core ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)

    // --- Navigation / Architecture ---
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.google.hilt)
 //   implementation(libs.androidx.hilt.navigation)
    kapt("com.google.dagger:hilt-android-compiler:2.57.2")
    kapt("androidx.room:room-compiler:2.8.3")

    // --- Data ---
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    // --- Ads ---
    implementation("com.google.android.gms:play-services-ads:24.7.0")

    // --- Charts ---
    implementation("io.github.bytebeats:compose-charts:0.2.1")

    // --- Firebase (BOM manages versions automatically) ---
    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth") // ✅ Firebase Auth




    // --- Google Sign-In ---
    implementation("com.google.android.gms:play-services-auth:21.4.0") // ✅ Google Sign-In for Firebase

    // --- Optional (for Credential Manager API – new Google sign-in flow) ---
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)



    // Declare the dependency for the Cloud Firestore library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-firestore")

    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel-compose:1.3.0")

}


