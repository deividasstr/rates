object Dependencies {

    private object Versions {

        const val kotlin = "1.3.50"
        const val buildGradle = "3.6.0-beta03"
        const val appCompat = "1.1.0"
        const val material = "1.0.0"
    }

    object Libraries {

        const val buildGradle = "com.android.tools.build:gradle:${Versions.buildGradle}"
        const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
        const val androidxAppcompat = "androidx.vectordrawable:vectordrawable:${Versions.appCompat}"
        const val material = "com.google.android.material:material:${Versions.material}"
    }
}