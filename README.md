# NEZA

A premium, dark-AMOLED Android AI assistant built with Kotlin, Jetpack Compose, Material 3, Hilt, Room, and Retrofit. Chat is wired to **both OpenAI and Google Gemini**. NEZA also listens for the wake phrase **"Hola NEZA"**, opens any installed app by voice, and can call your contacts.

## What's implemented in this milestone

- ✅ Project scaffold: Gradle Kotlin DSL, Hilt DI, MVVM, clean package structure
- ✅ **Onboarding screen**: requests mic, contacts, call, and notification permissions on first launch
- ✅ **NEZA avatar**: stylized animated face (blinking eyes, speaking mouth, listening/thinking glow ring) on the home screen
- ✅ Chat screen: real network calls to OpenAI (`gpt-4o-mini`) and Gemini (`gemini-1.5-flash`), persisted locally in Room
- ✅ **Voice commands**: say or type "open WhatsApp" / "call Rahul" — NEZA opens the app or dials the contact directly instead of asking the AI
- ✅ **Background wake-word service**: a foreground service listens for "Hola NEZA" and executes the command that follows (Android requires a visible notification while doing this — see note below)
- ✅ Settings screen: enter OpenAI + Gemini API keys, switch active provider
- ✅ GitHub Actions CI: lint, unit tests, debug APK build on every push; signed release APK when keystore secrets are configured

## Important platform limits (not bugs — Android enforces these for every app)

- **Background mic use always shows a notification.** Android does not allow any app to hide this; it's a privacy protection you cannot opt out of.
- **NEZA cannot run while the phone is fully powered off.** It restarts automatically on reboot (`BootReceiver`) and keeps running in the background while the phone is on, including with the screen off.
- **The avatar is a 2D animated illustration**, not a rigged 3D character — that requires external 3D/motion assets that can't be generated as code alone.
- **QUERY_ALL_PACKAGES** is required to see/open the full app list on Android 11+; this is fine for a sideloaded APK but would need justification for a Play Store listing.

## Not yet built (roadmap)

Screen analysis (MediaProjection — requires a fresh consent prompt every session, by OS design), OCR (ML Kit), CameraX (scan/QR/barcode), floating overlay bubble, clipboard AI, file assistant, browser assistant, PIN/biometric lock, streaming token-by-token responses, markdown/code rendering in chat, text-to-speech replies.

## Setup

1. Clone the repo and open it in Android Studio (Koala or newer).
2. Let Gradle sync. If you don't have a `gradlew` wrapper jar yet, run `gradle wrapper` once with a local Gradle 8.7+ install to generate it (not included in this scaffold since it's a binary file).
3. Run the app on a device/emulator (API 26+).
4. On first launch, grant the requested permissions.
5. In-app: go to **Settings**, paste your OpenAI API key and/or Gemini API key, pick your active provider, then go to **Chat**.

Get keys here:
- OpenAI: https://platform.openai.com/api-keys
- Gemini: https://aistudio.google.com/apikey

## CI / Release builds

The workflow at `.github/workflows/android-build.yml` runs lint + unit tests + debug build on every push. To get a **signed release APK** as well, add these repo secrets:

| Secret | Value |
|---|---|
| `KEYSTORE_BASE64` | your `.jks` keystore, base64-encoded |
| `KEYSTORE_PASSWORD` | keystore password |
| `KEY_ALIAS` | signing key alias |
| `KEY_PASSWORD` | signing key password |

## Security note

API keys are stored locally on-device via DataStore Preferences and are sent directly from the device to the provider you choose — never through a NEZA-owned backend. Contacts and app-list access are used only to fulfil the specific "open X" / "call X" command you gave.
