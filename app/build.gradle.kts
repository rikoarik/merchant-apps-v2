import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.20"
}

apply(from = "build-output.gradle")

android {
    namespace = "com.solusinegeri.merchant3"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.solusinegeri.merchant3"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "environment"
    
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            
            buildConfigField("String", "BASE_URL", "\"https://api.stg.solusinegeri.com/\"")
            buildConfigField("boolean", "IS_DEBUG", "true")
            buildConfigField("boolean", "ENABLE_LOGGING", "true")
            buildConfigField("String", "FLAVOR", "\"dev\"")
        }
        
        create("prod") {
            dimension = "environment"
            
            buildConfigField("String", "BASE_URL", "\"https://api.solusinegeri.com/\"")
            buildConfigField("boolean", "IS_DEBUG", "false")
            buildConfigField("boolean", "ENABLE_LOGGING", "false")
            buildConfigField("String", "FLAVOR", "\"prod\"")
        }
    }
    
    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }
}
kotlin {
    // Opsional tapi disarankan biar toolchain konsisten
    jvmToolchain(17)

    compilerOptions {
        // Pengganti `kotlinOptions.jvmTarget = "17"`
        jvmTarget.set(JvmTarget.JVM_17)

        // Jika butuh argumen tambahan, bisa di sini:
        // freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.androidx.swiperefreshlayout)
    
    // MVVM Dependencies
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.lifecycle.runtime)
    
    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    
    // Lottie
    implementation(libs.lottie)

    // Chucker - Network Inspector
    debugImplementation(libs.chucker)
    releaseImplementation(libs.chucker.no.op)
    
    // Image Loading
    implementation(libs.glide)
    
    // Security
    implementation(libs.androidx.security.crypto)
    
    // Navigation Component
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    
    // Jetpack Compose
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material)
    //noinspection GradleDependency
    implementation(libs.androidx.activity.compose)
    //noinspection GradleDependency
    implementation(libs.androidx.runtime.livedata)
    //noinspection GradleDependency
    implementation(libs.androidx.navigation.compose)
    
    // AAChartCore-Kotlin - Modern Chart Library
    implementation("com.github.AAChartModel:AAChartCore-Kotlin:7.5.0")
    
    // Date Picker
    implementation(libs.material.v1110)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}