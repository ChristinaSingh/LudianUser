plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")

}

android {
    namespace = "com.ludian"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ludian"
        minSdk = 26
        targetSdk = 34
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures{
        dataBinding = true
        viewBinding = true
        buildConfig = true

    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    //for dynamic size in ui
    implementation("com.intuit.sdp:sdp-android:1.1.0")

    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation("com.github.MohammedAlaaMorsi:RangeSeekBar:1.0.6")


   // For sliding image
    //implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.tbuonomo:dotsindicator:5.0")


    implementation("com.github.ybq:Android-SpinKit:1.4.0")

    val hilt_version="2.48"
    implementation("com.google.dagger:hilt-android:$hilt_version")
    kapt("com.google.dagger:hilt-compiler:$hilt_version")

    //retrofit api calling
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")


    // loading animation effects
    implementation("com.airbnb.android:lottie:3.4.0")


    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.0")

    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")


    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging-ktx:24.0.1")
    implementation("com.google.firebase:firebase-crashlytics")


    //work manager
    implementation("androidx.work:work-runtime-ktx:2.8.1")

    // for place api for location
    implementation("com.google.android.libraries.places:places:3.5.0")

    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.firebase:firebase-database:21.0.0")


    implementation("androidx.paging:paging-runtime-ktx:3.3.5")


}