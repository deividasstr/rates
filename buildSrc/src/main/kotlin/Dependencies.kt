import org.gradle.api.artifacts.dsl.DependencyHandler

object Dependencies {

    private object Versions {

        const val kotlin = "1.3.60"
        const val buildGradle = "3.6.0-beta04"
        const val material = "1.0.0"
        const val retrofit = "2.6.2"
        const val ktx = "1.0.1"
        const val retrofitCoroutinesAdapter = "0.9.2"
        const val mockWebServer = "4.2.2"
        const val moshi = "1.9.1"
        const val coroutines = "1.3.2"
        const val lifeCycleRc = "2.2.0-rc02"
        const val lifeCycle = "2.1.0"
        const val room = "2.2.1"
        const val ticker = "2.0.2"
        const val adapterDelegates = "4.2.0"
        const val koin = "2.0.1"
        const val constraintLayout = "2.0.0-beta3"
        const val currencies = "1.1.9"
        const val loggingInterceptor = "4.2.1"
        const val junit = "4.12"
        const val mockk = "1.9.2"
        const val kluent = "1.57"
        const val timber = "4.7.1"
        const val androidxTest = "1.2.0"
        const val liveDataTest = "1.1.1"
    }

    object Libraries {

        const val buildGradle = "com.android.tools.build:gradle:${Versions.buildGradle}"
        const val kotlinGradlePlugin =
            "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
        const val ktx = "androidx.core:core-ktx:${Versions.ktx}"
        const val material = "com.google.android.material:material:${Versions.material}"
        const val constraintLayout =
            "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"

        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        const val retrofitCoroutinesAdapter =
            "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:" +
                Versions.retrofitCoroutinesAdapter
        const val mockWebServer = "com.squareup.okhttp3:mockwebserver:${Versions.mockWebServer}"
        const val moshiRetrofitAdapter =
            "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
        const val loggingInterceptor =
            "com.squareup.okhttp3:logging-interceptor:${Versions.loggingInterceptor}"

        const val moshi = "com.squareup.moshi:moshi:${Versions.moshi}"
        const val moshiKotlin = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
        const val moshiKotlinCodegen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"

        const val coroutinesCore =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
        const val coroutinesAndroid =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
        const val coroutinesTest =
            "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"

        const val lifecycle = "androidx.lifecycle:lifecycle-extensions:${Versions.lifeCycleRc}"
        const val lifecycleLiveData = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifeCycleRc}"
        const val lifecycleCompiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifeCycleRc}"
        const val lifecycleTest = "androidx.arch.core:core-testing:${Versions.lifeCycle}"
        const val liveDataTest = "com.jraska.livedata:testing-ktx:${Versions.liveDataTest}"

        const val room = "androidx.room:room-runtime:${Versions.room}"
        const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
        const val roomCoroutines = "androidx.room:room-ktx:${Versions.room}"
        const val roomTest = "androidx.room:room-testing:${Versions.room}"

        const val ticker = "com.robinhood.ticker:ticker:${Versions.ticker}"
        const val adapterDelegates =
            "com.hannesdorfmann:adapterdelegates4-kotlin-dsl:${Versions.adapterDelegates}"
        const val currencies =
            "com.github.midorikocak:currency-picker-android:${Versions.currencies}"

        const val koin = "org.koin:koin-core:${Versions.koin}"
        const val koinExt = "org.koin:koin-core-ext:${Versions.koin}"
        const val koinTest = "org.koin:koin-test:${Versions.koin}"
        const val koinAndroidxScope = "org.koin:koin-androidx-scope:${Versions.koin}"
        const val koinAndroidxViewmodel = "org.koin:koin-androidx-viewmodel:${Versions.koin}"
        const val koinAndroidxExt = "org.koin:koin-androidx-ext:${Versions.koin}"

        const val junit = "junit:junit:${Versions.junit}"
        const val kluent = "org.amshove.kluent:kluent-android:${Versions.kluent}"
        const val mockk = "io.mockk:mockk:${Versions.mockk}"
        const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

        const val androidTest = "androidx.test:core:${Versions.androidxTest}"
        const val testRunner = "androidx.test:runner:${Versions.androidxTest}"
        const val testRules = "androidx.test:rules:${Versions.androidxTest}"
    }
}

// When the project has more modules, definitely do not group implementation dependencies with test and androidTest ones
fun DependencyHandler.implementNetworking() {
    add("implementation", Dependencies.Libraries.retrofit)
    add("implementation", Dependencies.Libraries.retrofitCoroutinesAdapter)
    add("implementation", Dependencies.Libraries.loggingInterceptor)
    add("implementation", Dependencies.Libraries.moshiRetrofitAdapter)
    add("testImplementation", Dependencies.Libraries.mockWebServer)
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
    add("androidTestImplementation", Dependencies.Libraries.coroutinesTest)
}

fun DependencyHandler.implementLifecycle() {
    add("implementation", Dependencies.Libraries.lifecycle)
    add("implementation", Dependencies.Libraries.lifecycleLiveData)
    add("kapt", Dependencies.Libraries.lifecycleCompiler)
    add("testImplementation", Dependencies.Libraries.lifecycleTest)
    add("testImplementation", Dependencies.Libraries.liveDataTest)
}

fun DependencyHandler.implementDb() {
    add("implementation", Dependencies.Libraries.room)
    add("kapt", Dependencies.Libraries.roomCompiler)
    add("implementation", Dependencies.Libraries.roomCoroutines)
    add("testImplementation", Dependencies.Libraries.roomTest)
    add("androidTestImplementation", Dependencies.Libraries.roomTest)
}

fun DependencyHandler.implementDi() {
    add("implementation", Dependencies.Libraries.koin)
    add("implementation", Dependencies.Libraries.koinExt)
    add("implementation", Dependencies.Libraries.koinAndroidxScope)
    add("implementation", Dependencies.Libraries.koinAndroidxViewmodel)
    add("implementation", Dependencies.Libraries.koinAndroidxExt)
    add("testImplementation", Dependencies.Libraries.koinTest)
}

fun DependencyHandler.implementTest() {
    add("testImplementation", Dependencies.Libraries.junit)
    add("testImplementation", Dependencies.Libraries.kluent)
    add("testImplementation", Dependencies.Libraries.mockk)

    // https://stackoverflow.com/questions/56571764/android-where-did-applicationprovider-go
    add("testImplementation", Dependencies.Libraries.androidTest)
}

fun DependencyHandler.implementAndroidTest() {
    add("androidTestImplementation", Dependencies.Libraries.junit)
    add("androidTestImplementation", Dependencies.Libraries.kluent)
    add("androidTestImplementation", Dependencies.Libraries.mockk)
    add("androidTestImplementation", Dependencies.Libraries.androidTest)
    add("androidTestImplementation", Dependencies.Libraries.testRunner)
    add("androidTestImplementation", Dependencies.Libraries.testRules)
}