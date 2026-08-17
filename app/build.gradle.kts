import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services) // Uncomment after adding google-services.json
}

android {
    namespace = "com.amstudio.lightbasket"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.amstudio.lightbasket"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }

        val supabaseUrl = localProperties.getProperty("SUPABASE_URL") ?: "https://your-project.supabase.co"
        val supabaseAnonKey = localProperties.getProperty("SUPABASE_ANON_KEY") ?: "your-anon-key"
        val locationIqKey = localProperties.getProperty("LOCATIONIQ_API_KEY") ?: "your-locationiq-key"

        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")
        buildConfigField("String", "LOCATIONIQ_API_KEY", "\"$locationIqKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.gson)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.play.services.location)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.glide)
    implementation(libs.photoview)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.shimmer)
    implementation("com.airbnb.android:lottie:6.4.0")
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")
    annotationProcessor(libs.glide.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}