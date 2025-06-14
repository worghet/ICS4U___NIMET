plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "app.nimet"
    compileSdk = 35

    defaultConfig {
        applicationId = "app.nimet"
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

    // for location
    implementation(libs.play.services.location)

    // for loading image from url
    implementation(libs.glide.v4151)
    annotationProcessor(libs.compiler.v4151)

    implementation(libs.glide)
    implementation(files("libs/gson-2.10.1.jar"))
    annotationProcessor(libs.glide.compiler)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}