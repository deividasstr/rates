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

    sourceSets["main"].java.srcDir("src/main/kotlin")
    sourceSets["test"].java.srcDir("src/test/kotlin")
    sourceSets["androidTest"].java.srcDir("src/androidTest/kotlin")
}

dependencies {
    implementation(Dependencies.Libraries.kotlinStdLib)
    implementation(Dependencies.Libraries.ktx)
    implementation(Dependencies.Libraries.material)
    implementation(Dependencies.Libraries.constraintLayout)
    implementation(Dependencies.Libraries.ticker)
    implementation(Dependencies.Libraries.adapterDelegates)

    //TODO: find libs handling currency parsing, currency names and country flags

    implementLifecycle()
    implementCoroutines()
    implementJsonParsing()
    implementNetworking()
    implementDb()
    implementDi()
}
