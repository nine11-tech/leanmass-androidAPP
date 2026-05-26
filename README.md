# LeanMass Calculator

LeanMass Calculator is a Kotlin Android mini-project for a mobile development course. It calculates lean body mass, gives immediate status feedback, and stores each user's calculation history locally.

## Features

- Secure sign up and login with SQLite-backed users.
- Passwords are stored with PBKDF2WithHmacSHA256 hashes and per-user random salts.
- Session routing from a splash screen using SharedPreferences.
- Lean Body Mass calculator using the Boer formulas.
- Immediate visual feedback: result satisfactory or result to monitor.
- Automatic local history persistence for every calculation.
- RecyclerView history screen with single deletion and clear-all confirmation.
- Profile screen with account data, total calculations, average LBM, last LBM, and logout.
- XML layouts with ViewBinding and Material Components.

## Tech Stack

- Kotlin
- Android XML views
- ViewBinding
- Material Components
- SQLiteOpenHelper for local persistence
- SharedPreferences for session state
- JUnit unit tests

## Boer Formulas

- Male: `LBM = (0.407 * weightKg) + (0.267 * heightCm) - 19.2`
- Female: `LBM = (0.252 * weightKg) + (0.473 * heightCm) - 48.3`

## Thresholds

- Male result is satisfactory when `LBM >= 38 kg`.
- Female result is satisfactory when `LBM >= 24 kg`.

Thresholds live in `LeanMassConfig`, so they can be adjusted without changing activity code.

## Screens

- Splash: checks whether a saved session exists.
- Login: authenticates an existing account.
- Register: creates a local account with hashed password storage.
- Home: shows account greeting, stats cards, calculator form, and latest result.
- History: lists saved calculations and supports deletion.
- Profile: shows account and stats, with logout.

## Build

```powershell
.\gradlew.bat build
```

## Run On A Physical Phone

1. Enable Developer Options on the phone.
2. Enable USB Debugging.
3. Connect the phone with USB.
4. Open the project in Android Studio.
5. Select the phone as the target device and press Run.

No emulator is required. SQLite is the main persistence system and works offline. Firebase or Firestore can be added later behind the repository layer if cloud sync becomes required.
