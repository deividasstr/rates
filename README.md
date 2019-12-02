# RevoRateLut

This project was completed using 
* Kotlin 
* Coroutines 
* Koin for DI 
* Android architecture components - livedata and viewmodel
* Room and SharedPreferences for cache
* Viewbinding, adapter delegates for UI
* Gradle scripts kotlin DSL
* mockk for mocking

One of the goals was to use unused before yet popular tech-stack (flow coroutines, koin, room, viewbinding, adapter delegates, gradle scripts kotlin DSL).

## What is over-killed

The description did not require caching (especially DB) and offline usage, visually nice network error handling, abstract architecture.

## What could have been done better

* Issues solved which are affecting your app (lag on scroll due to updating of items, numeric keyboard chang to text on scroll down)
* Input handling and validation (limiting the input, preventing the adding of trailing or starting zeroes, several commas, locale-specific punctuation)
* UI tests (although there is little logic there), broader unit tests, especially of formatting and calculations.
* Finer-grained commits
* Docs?
