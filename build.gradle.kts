import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath (Dependencies.Libraries.buildGradle)
        classpath (Dependencies.Libraries.kotlinGradlePlugin)

    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

tasks.withType(Test::class.java) {
    testLogging {
        events = setOf(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.STANDARD_ERROR,
            TestLogEvent.STANDARD_OUT)
        exceptionFormat = TestExceptionFormat.FULL
        showCauses = true
        showExceptions = true
        showStackTraces = true
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}