# VoicePing (Notification Voice Announcer)

Hands-free Android app that reads your notifications aloud using Text-to-Speech.

## Features
- Announces WhatsApp, Gmail, Telegram messages by sender name
- Promotional notification detection
- OTP filter — speaks "OTP message from [Bank]" without reading digits
- App whitelist — choose which apps get announced
- Quiet hours toggle
- Screen-off only mode
- Bluetooth/headset only mode
- Duplicate suppression

## How to Install
1. Download `app-debug.apk` from the [Latest Release](https://github.com/niteshnanu12/notification-voice-announcer/releases)
2. On your Android phone go to Settings → Security → Enable "Install Unknown Apps"
3. Open the downloaded APK and install
4. Open the app and grant Notification Access permission

## Requirements
- Android 5.0 (API 21) or higher

---

## Developer Instructions

### Step 4: Build a Release APK
In Android Studio:
1. Build → Build Bundle(s) / APK(s) → Build APK(s)
2. Wait for "Build Successful"
3. APK location: `D:\Projects\NotificationAnnouncer\app\build\outputs\apk\debug\app-debug.apk`

### Step 5: Create a GitHub Release
1. Go to your GitHub repo page
2. Click "Releases" on the right side → "Create a new release"
3. Fill in:
   - **Tag version**: `v1.0.0`
   - **Release title**: `VoicePing v1.0.0`
   - **Description**: (Copy the Features and Install sections above)
4. Drag and drop your `app-debug.apk` file into the "Attach binaries" area
5. Click "Publish release"
