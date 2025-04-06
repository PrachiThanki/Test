plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.chathub"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.chathub"
        minSdk = 24
        targetSdk = 35
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
}


dependencies {

    // AndroidX Libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Firebase (using BOM for version management)
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.firebaseui:firebase-ui-firestore:8.0.2")

    // Utility Libraries
    implementation("com.hbb20:ccp:2.7.3") // Country Code Picker
    implementation("com.github.bumptech.glide:glide:4.16.0") // Image Loading
    implementation("com.github.dhaval2404:imagepicker:2.1") // Image Picker
    implementation("com.google.android.gms:play-services-safetynet:18.0.1")


    implementation ("com.google.android.gms:play-services-safetynet:17.0.0")
    implementation ("com.google.android.gms:play-services-safetynet:17.0.0")
    implementation ("com.google.firebase:firebase-auth:22.1.2")
}