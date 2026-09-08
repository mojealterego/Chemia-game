# CHEMIA — Native Android

CHEMIA is now structured as a native Android application using Kotlin + Jetpack Compose.

## Build

Use Android Studio with JDK 17+ and run:

```bash
./gradlew :app:assembleDebug
```

APK output:

`app/build/outputs/apk/debug/app-debug.apk`

The legacy HTML/PWA prototype remains in the repository only as historical material; the Android application lives under `app/` and uses `pl.chemia.game` as its application ID.
