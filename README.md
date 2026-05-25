# Resultant Prism Calculator

Native Android app for ophthalmic optics — calculates the resultant prism for two eyes and the equal-distribution variant.

## Stack
- Kotlin + Jetpack Compose
- Material Design 3
- Single Activity (`MainActivity`)
- `minSdk = 26`, `targetSdk = 34`, `compileSdk = 34`

## Build
1. Open the `prism-calculator/` folder in Android Studio Hedgehog or newer.
2. Let Gradle sync (Android Gradle Plugin 8.2.2, Kotlin 1.9.22, Gradle 8.4).
3. Run the `app` configuration on a device/emulator with API 26+.

## Structure
```
app/src/main/java/com/nevzorovlabs/prismcalc/
  MainActivity.kt
  ui/
    theme/
      Theme.kt
      Color.kt
    PrismScreen.kt
    ResultCard.kt
  logic/
    PrismCalculator.kt
```

`PrismCalculator.kt` is pure Kotlin and has no Android dependencies — easy to unit test on the JVM.
