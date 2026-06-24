# VoicePing 🔔🗣️

**VoicePing** is a lightweight Android application that announces incoming notifications aloud using Android Text-to-Speech (TTS). Stay informed without constantly checking your phone—perfect for driving, working, exercising, or multitasking.

---

## ✨ Key Features

### 📢 Smart Notification Announcements
* Reads notifications aloud from supported apps.
* Announces sender names for messaging applications.
* Example: "WhatsApp message from Nitesh"

### 💬 Messaging App Support
Supports notification announcements from:
* WhatsApp
* Telegram
* Gmail
* SMS Messages
* Other supported Android apps

### 🔒 Privacy-Focused OTP Detection
* Automatically detects OTP and verification messages.
* Announces: "OTP message received from [Bank/App]"
* Never reads sensitive OTP digits aloud.

### 🚫 Promotional Message Filtering
* Detects promotional and marketing notifications.
* Prevents unnecessary announcements and interruptions.

### 🎯 App Whitelist Control
* Choose exactly which applications can be announced.

### 🌙 Quiet Hours
* Configure silent periods during specific hours.

### 📱 Screen-Off Mode
* Announces notifications only when the screen is off.

### 🎧 Bluetooth & Headset Mode
* Announcements only through connected Bluetooth devices or wired headsets.

### 🔄 Duplicate Notification Suppression
* Prevents repeated announcements of the same notification.

---

## 🚀 Installation

1. Download `app-voiceping.apk` from the [Latest Release](https://github.com/niteshnanu12/notification-voice-announcer/releases).
2. On your Android phone go to **Settings → Security → Enable "Install Unknown Apps"**.
3. Open the downloaded APK and install.
4. Launch VoicePing and grant **Notification Access** permission.

---

## 📋 Requirements
- **Android OS**: Android 5.0 (API 21+) or higher.
- **Permission**: Notification Access.

---

## 🛠️ Developer Instructions

### Step 1: Build the APK
In Android Studio:
1. Navigate to: **Build → Generate App Bundles or APKs → Build APK(s)**.
2. Wait for "Build Successful".
3. Locate the generated APK at: `app/build/outputs/apk/debug/app-voiceping.apk`

### Step 2: Create a GitHub Release
1. Go to your GitHub repo page.
2. Click **Releases** → **Create a new release**.
3. Fill in:
   - **Tag version**: `v1.0.0`
   - **Release title**: `VoicePing v1.0.0`
4. Drag and drop `app-voiceping.apk` into the release assets.
5. Click **Publish release**.

---

## 👨‍💻 Developer
**P. Nitesh kumar**
Built to make notifications accessible, safer, and truly hands-free.
