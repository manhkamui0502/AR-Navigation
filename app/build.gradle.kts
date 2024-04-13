plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.arnavigation"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.arnavigation"
        minSdk = 30
        targetSdk = 32
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-inappmessaging:20.4.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.mapbox.maps:android:10.14.1")
    implementation("com.mapbox.plugin:maps-annotation:10.15.0")
    implementation("com.mapbox.navigation:android:2.18.0")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    implementation("com.google.android.gms:play-services-location:20.0.0")


    implementation("com.mapbox.maps:base:10.15.0")
    implementation("com.mapbox.search:autofill:1.0.0-rc.6")
    implementation("com.mapbox.search:place-autocomplete:1.0.0-rc.6")
    implementation("com.mapbox.search:offline:1.0.0-rc.6")
    implementation("com.mapbox.search:mapbox-search-android:1.0.0-rc.6")
    implementation("com.mapbox.search:mapbox-search-android-ui:1.0.0-rc.6")
    implementation("com.mapbox.search:discover:1.0.0-beta.44")
    implementation("com.mapbox.navigation:android:2.18.0")
    //implementation("com.mapbox.mapboxsdk:mapbox-android-navigation:0.42.6")
}