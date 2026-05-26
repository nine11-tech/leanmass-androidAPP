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
.\gradlew.bat testDebugUnitTest
.\gradlew.bat build
.\gradlew.bat assembleDebug
```

## Run On A Physical Phone

1. Enable Developer Options on the phone.
2. Enable USB Debugging.
3. Connect the phone with USB.
4. Open the project in Android Studio.
5. Select the phone as the target device and press Run.

No emulator is required. SQLite is the main persistence system and works offline. Firebase or Firestore can be added later behind the repository layer if cloud sync becomes required.

## Device Check

The app was installed and tested successfully on a real Android phone.

## Mobile Security Hardening

This version was hardened for a Mobile Security mini-project. The goal is to analyze an existing Android app with OWASP MASVS categories, identify realistic weaknesses, and implement corrective controls without breaking the original user experience.

| Initial weakness | Risk | MASVS category | Implemented correction | Main files changed |
|---|---|---|---|---|
| Local SQLite and session data could be included in backup rules | Exposure of account/session and health-related calculation history through backup or transfer | MASVS-STORAGE, MASVS-PRIVACY | Disabled `allowBackup`, excluded databases/preferences/files from backup and data extraction | `AndroidManifest.xml`, `backup_rules.xml`, `data_extraction_rules.xml` |
| Password policy only checked length | Weak user passwords easier to guess | MASVS-AUTH | Enforced 8+ chars, uppercase, lowercase, digit, special character, email/name/confirmation validation | `AuthValidator.kt`, `AuthRepository.kt`, `RegisterActivity.kt` |
| Password hashing needed explicit cryptographic documentation and tests | Risk of unclear or weak password storage | MASVS-CRYPTO, MASVS-AUTH | PBKDF2WithHmacSHA256, SecureRandom salt, Base64 hash/salt, 150000 iterations, 256-bit key, constant-time comparison | `PasswordHasher.kt`, `PasswordHasherTest.kt` |
| Session stored only user id and never expired | Long-lived local session if device is shared or lost | MASVS-AUTH, MASVS-STORAGE | Session stores minimal user id plus login timestamp, expires after 24 hours, logout clears all session data | `SessionManager.kt`, `SessionPolicy.kt`, `SplashActivity.kt` |
| Sensitive screens allowed screenshots and screen recording | Personal profile and health history may be captured by other apps or accidental screenshots | MASVS-PLATFORM, MASVS-PRIVACY | `FLAG_SECURE` applied to login, register, home, history, profile, and splash screens | `SecureScreenHelper.kt`, UI activities |
| No runtime environment awareness | Debug/root environments are not highlighted during demonstration | MASVS-RESILIENCE, MASVS-CODE | Non-blocking root/debug/debuggable status appears in Profile | `SecurityChecks.kt`, `ProfileActivity.kt`, `activity_profile.xml` |
| SQLite access needed explicit user scoping evidence | Cross-user data exposure if queries are not scoped | MASVS-STORAGE, MASVS-CODE, MASVS-PRIVACY | History read/delete/clear operations are parameterized and scoped by authenticated `userId` | `CalculationDao.kt`, `CalculationRepository.kt` |
| Logging policy was not documented | Sensitive values could leak through logs during development | MASVS-CODE, MASVS-PRIVACY | Verified no password/hash/salt/database values are logged; no sensitive `Log` or `println` usage added | project source |

Security controls included:

- Password hashing with `PBKDF2WithHmacSHA256`.
- Per-user `SecureRandom` salt encoded with Base64.
- Strong password policy and generic login failure messages.
- Session expiration after 24 hours.
- Android backup disabled and sensitive backup exclusions documented.
- `FLAG_SECURE` screenshot/screen-recording protection on sensitive screens.
- SQLite queries and delete operations scoped to the logged-in user.
- Root/debug/debuggable environment awareness in the Profile screen.
- No sensitive logging of passwords, hashes, salts, or local database values.
