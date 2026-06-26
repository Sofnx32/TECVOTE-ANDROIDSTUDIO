plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace   = "pe.tecvote.enrolamiento"
    compileSdk  = 36

    defaultConfig {
        applicationId          = "pe.tecvote.enrolamiento"
        minSdk                 = 26
        targetSdk              = 36
        versionCode            = 1
        versionName            = "1.0"
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

    buildFeatures {
        compose = true
    }
}

dependencies {
    // ── Core & Compose ────────────────────────────────────────────────
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.foundation:foundation:1.7.0")
    implementation("androidx.compose.material:material-icons-extended:1.7.0")

    // ── Red (Retrofit & OkHttp) ───────────────────────────────────────
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ── Corrutinas    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation(libs.androidx.tv.material)

    // ── CameraX (Actualizado a 1.4.1 para soporte estricto de 16 KB) ──
    val camerax_version = "1.4.1"
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation("androidx.camera:camera-video:${camerax_version}")
    implementation("androidx.camera:camera-view:${camerax_version}")
    implementation("androidx.camera:camera-extensions:${camerax_version}")

    // ── Biometría
    implementation("androidx.biometric:biometric:1.2.0-alpha05")

    // ── Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation("com.google.mlkit:face-detection:16.1.7")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    implementation("androidx.camera:camera-camera2:${camerax_version}")
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation("androidx.camera:camera-view:${camerax_version}")


    // ── Google Maps & Location ─────────────────────────────────────────
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.1.0")
    implementation("com.google.maps.android:maps-compose:4.3.3")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // ── QR Generator ──────────────────────────────────────────────────
    implementation("com.google.zxing:core:3.5.2")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    // ── Navigation Compose ────────────────────────────────────────────
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ── PDF Generation (para constancia) ──────────────────────────────
    implementation("com.tom-roush:pdfbox-android:2.0.27.0")

    // ── Coil para cargar imágenes (QR, mapas estáticos) ───────────────
    implementation("io.coil-kt:coil-compose:2.6.0")
}