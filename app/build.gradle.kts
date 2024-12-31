import com.android.build.gradle.internal.tasks.databinding.DataBindingGenBaseClassesTask
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompileTool
import java.util.Locale

//https://github.com/google/dagger/issues/4049#issuecomment-1837479042 참고
androidComponents {
    onVariants(selector().all()) { variant ->
        afterEvaluate {
            project.tasks.getByName(
                "ksp${
                    variant.name.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                }Kotlin"
            ) {
                val dataBindingTask =
                    try {
                        val taskName = "dataBindingGenBaseClasses${
                            variant.name.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.getDefault()
                                ) else it.toString()
                            }
                        }"
                        project.tasks.getByName(taskName) as DataBindingGenBaseClassesTask
                    } catch (e: UnknownTaskException) {
                        return@getByName
                    }

                project.tasks.getByName("ksp${variant.name.capitalize()}Kotlin") {
                    (this as AbstractKotlinCompileTool<*>).setSource(dataBindingTask.sourceOutFolder)
                }
            }
        }
    }
}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.eighteen.eighteenandroid"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.eighteen.eighteenandroid"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField(
            "String",
            "BASE_URL",
            com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir)
                .getProperty("BASE_URL")
        )
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.fragment:fragment-ktx:1.7.0")
    implementation("androidx.databinding:databinding-runtime:8.4.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")


    //fragment
    implementation("androidx.fragment:fragment-ktx:1.3.6")

    //retrofit2
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")

    //moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")

    //okhttp3
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    //navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    //hilt
    implementation("com.google.dagger:hilt-android:2.48")
    ksp("com.google.dagger:hilt-android-compiler:2.48")

    //kotlin ext
    implementation("androidx.fragment:fragment-ktx:1.7.0")

    //viewpager2
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    //media3
    val media3Version = "1.3.1"
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    implementation("androidx.media3:media3-ui:$media3Version")
    implementation("androidx.media3:media3-common:$media3Version")

    //ffmpeg
    implementation("com.arthenica:ffmpeg-kit-full:6.0-2")

    // Flow Binding
    val flowbinding_version = "1.2.0"
    implementation("io.github.reactivecircus.flowbinding:flowbinding-android:${flowbinding_version}")

    // Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    //DataStore
    val datastoreVersion = "1.1.1"
    implementation("androidx.datastore:datastore-preferences:$datastoreVersion")

    //lottie
    val lottieVersion = "5.2.0"
    implementation("com.airbnb.android:lottie:$lottieVersion")
}