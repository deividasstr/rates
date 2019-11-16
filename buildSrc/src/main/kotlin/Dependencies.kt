import org.gradle.api.artifacts.dsl.DependencyHandler

object Dependencies {

    private object Versions {

        const val kotlin = "1.3.50"
        const val buildGradle = "3.6.0-beta03"
        const val material = "1.0.0"
        const val retrofit = "2.6.2"
        const val ktx = "1.0.1"
        const val retrofitCoroutinesAdapter = "0.9.2"
        const val mockWebServer = "4.2.2"
        const val moshi = "1.9.1"
        const val coroutines = "1.3.2"
        const val lifeCycle = "2.1.0"
        const val room = "2.2.1"
        const val ticker = "2.0.2"
        const val adapterDelegates = "4.2.0"
        const val koin = "2.0.1"
    }

    object Libraries {

        const val buildGradle = "com.android.tools.build:gradle:${Versions.buildGradle}"
        const val kotlinGradlePlugin =
            "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
        const val ktx = "androidx.core:core-ktx:${Versions.ktx}"
        const val material = "com.google.android.material:material:${Versions.material}"

        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        const val retrofitCoroutinesAdapter =
            "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:" +
                Versions.retrofitCoroutinesAdapter
        const val mockWebServer = "com.squareup.okhttp3:mockwebserver:${Versions.mockWebServer}"
        const val moshiRetrofitAdapter = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"

        const val moshi = "com.squareup.moshi:moshi:${Versions.moshi}"
        const val moshiKotlin = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
        const val moshiKotlinCodegen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"
        //kapt

        const val coroutinesCore =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
        const val coroutinesAndroid =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
        const val coroutinesTest =
            "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"

        const val lifecycle = "androidx.lifecycle:lifecycle-extensions:${Versions.lifeCycle}"
        const val lifecycleCompiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifeCycle}"
        //kapt
        const val lifecycleTest = "androidx.arch.core:core-testing:${Versions.lifeCycle}"

        const val room = "androidx.room:room-runtime:${Versions.room}"
        const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
        //kapt
        const val roomCoroutines = "androidx.room:room-ktx:${Versions.room}"
        const val roomTest = "androidx.room:room-testing:${Versions.room}"

        const val ticker = "com.robinhood.ticker:ticker:${Versions.ticker}"
        const val adapterDelegates =
            "com.hannesdorfmann:adapterdelegates4-kotlin-dsl:${Versions.adapterDelegates}"

        const val koin = "org.koin:koin-core:${Versions.koin}"
        const val koinExt = "org.koin:koin-core-ext:${Versions.koin}"
        const val koinTest = "org.koin:koin-test:${Versions.koin}"
        const val koinAndroidxScope = "org.koin:koin-androidx-scope:${Versions.koin}"
        const val koinAndroidxViewmodel = "org.koin:koin-androidx-viewmodel:${Versions.koin}"
        const val koinAndroidxExt = "org.koin:koin-androidx-ext:${Versions.koin}"
    }
}

fun DependencyHandler.implementNetworking() {
    add("implementation", Dependencies.Libraries.retrofit)
    add("implementation", Dependencies.Libraries.retrofitCoroutinesAdapter)
    add("implementation", Dependencies.Libraries.mockWebServer)
    add("testImplementation", Dependencies.Libraries.moshiRetrofitAdapter)
}

fun DependencyHandler.implementJsonParsing() {
    add("implementation", Dependencies.Libraries.moshi)
    add("implementation", Dependencies.Libraries.moshiKotlin)
    add("kapt", Dependencies.Libraries.moshiKotlinCodegen)
}

fun DependencyHandler.implementCoroutines() {
    add("implementation", Dependencies.Libraries.coroutinesCore)
    add("implementation", Dependencies.Libraries.coroutinesAndroid)
    add("testImplementation", Dependencies.Libraries.coroutinesTest)
}

fun DependencyHandler.implementLifecycle() {
    add("implementation", Dependencies.Libraries.lifecycle)
    add("kapt", Dependencies.Libraries.lifecycleCompiler)
    add("testImplementation", Dependencies.Libraries.lifecycleTest)
}

fun DependencyHandler.implementDb() {
    add("implementation", Dependencies.Libraries.room)
    add("kapt", Dependencies.Libraries.roomCompiler)
    add("implementation", Dependencies.Libraries.roomCoroutines)
    add("testImplementation", Dependencies.Libraries.roomTest)
}

fun DependencyHandler.implementDi() {
    add("implementation", Dependencies.Libraries.koin)
    add("implementation", Dependencies.Libraries.koinExt)
    add("implementation", Dependencies.Libraries.koinAndroidxScope)
    add("implementation", Dependencies.Libraries.koinAndroidxViewmodel)
    add("implementation", Dependencies.Libraries.koinAndroidxExt)
    add("testImplementation", Dependencies.Libraries.koinTest)
}