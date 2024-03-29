
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdkVersion(29)
    buildToolsVersion("29.0.2")
    defaultConfig {
        applicationId = "com.deividasstr.revoratelut"
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "baseUrl", Obfuscator.getString(SecretProperties.baseUrl))
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    // Required by live data observation
    kotlinOptions.jvmTarget = "1.8"

    viewBinding.isEnabled = true

    sourceSets["main"].java.srcDir("src/main/kotlin")
    sourceSets["test"].java.srcDir("src/test/kotlin")
    sourceSets["androidTest"].java.srcDir("src/androidTest/kotlin")
    sourceSets["test"].java.srcDir("src/sharedTestDir/kotlin")
    sourceSets["androidTest"].java.srcDir("src/sharedTestDir/kotlin")

    createSigningConfigs()
    setSigningConfigToRelease()
}

fun BaseAppModuleExtension.createSigningConfigs() {
    signingConfigs {
        create("release") {
            keyAlias = SecretProperties.keyAlias
            keyPassword = SecretProperties.keyPassword
            storeFile = rootProject.file(SecretProperties.storeFile)
            storePassword = SecretProperties.storePassword
        }
    }
}

fun BaseAppModuleExtension.setSigningConfigToRelease() {
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

dependencies {
    implementation(Dependencies.Libraries.kotlinStdLib)
    implementation(Dependencies.Libraries.ktx)
    implementation(Dependencies.Libraries.material)
    implementation(Dependencies.Libraries.constraintLayout)
    implementation(Dependencies.Libraries.circularImageView)
    implementation(Dependencies.Libraries.adapterDelegates)
    implementation(Dependencies.Libraries.currencies)

    implementation(Dependencies.Libraries.timber)

    implementLifecycle()
    implementCoroutines()
    implementJsonParsing()
    implementNetworking()
    implementDb()
    implementDi()

    implementTest()
    implementAndroidTest()
}