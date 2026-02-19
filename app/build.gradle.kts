import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.dagger.hilt)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.uansari.moviewise"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.uansari.moviewise"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.uansari.moviewise.utils.HiltTestRunner"

        buildConfigField(
            "String", "TMDB_API_KEY", "\"${localProperties.getProperty("TMDB_API_KEY", "")}\""
        )
        buildConfigField(
            "String", "TMDB_BASE_URL", "\"https://api.themoviedb.org/3/\""
        )
        buildConfigField(
            "String", "TMDB_IMAGE_BASE_URL", "\"https://image.tmdb.org/t/p/\""
        )

    }
    signingConfigs {
        create("release") {
            // Read from environment variables (for CI) or local.properties (for local builds)
            storeFile = file(
                System.getenv("KEYSTORE_FILE") ?: project.findProperty("KEYSTORE_FILE")
                ?: "release-keystore.jks"
            )
            storePassword = System.getenv("KEYSTORE_PASSWORD")
                ?: project.findProperty("KEYSTORE_PASSWORD") as String?
            keyAlias = System.getenv("KEY_ALIAS") ?: project.findProperty("KEY_ALIAS") as String?
                    ?: "moviewise"
            keyPassword =
                System.getenv("KEY_PASSWORD") ?: project.findProperty("KEY_PASSWORD") as String?
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
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
        buildConfig = true
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
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material.icons)

    implementation(libs.androidx.ui)
    implementation(libs.core.ktx)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.junit.ktx)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)

    implementation(libs.navigation.compose)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.google.truth)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.robolectric)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.paging.common)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    androidTestImplementation(libs.room.testing)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.google.truth)
    androidTestImplementation(libs.turbine)
    androidTestImplementation(libs.androidx.arch.core.testing)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.navigation.testing)
    kspAndroidTest(libs.hilt.android.compiler)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}