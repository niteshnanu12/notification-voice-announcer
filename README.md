# VoicePing 🔔🗣️

**VoicePing** is a lightweight Android application that announces incoming notifications aloud using Android Text-to-Speech (TTS). Stay informed without constantly checking your phone—perfect for driving, working, exercising, or multitasking.

---

## ✨ Key Features

### 📢 Smart Notification Announcements

* Reads notifications aloud from supported apps.
* Announces sender names for messaging applications.
* Works seamlessly in the background.

### 💬 Messaging App Support

Supports notification announcements from:

* WhatsApp
* Telegram
* Gmail
* SMS Messages
* Other supported Android apps

### 🔒 Privacy-Focused OTP Detection

* Automatically detects OTP and verification messages.
* Announces:

  > "OTP message received from [Bank/App]"
* Never reads sensitive OTP digits aloud.

### 🚫 Promotional Message Filtering

* Detects promotional and marketing notifications.
* Prevents unnecessary announcements and interruptions.

### 🎯 App Whitelist Control

* Choose exactly which applications can be announced.
* Disable announcements for unwanted apps.

### 🌙 Quiet Hours

* Configure silent periods during specific hours.
* Prevents announcements while sleeping or during meetings.

### 📱 Screen-Off Mode

* Announces notifications only when the screen is off.
* Reduces unnecessary interruptions while actively using the device.

### 🎧 Bluetooth & Headset Mode

* Announcements only through connected Bluetooth devices or wired headsets.
* Ideal for driving and hands-free usage.

### 🔄 Duplicate Notification Suppression

* Prevents repeated announcements of the same notification.
* Provides a cleaner user experience.

---

## 🚀 Installation

### Option 1: Download APK

1. Download the latest APK from the project's Releases page.
2. Open **Settings → Security → Install Unknown Apps**.
3. Enable installation permission for your browser or file manager.
4. Install the APK.
5. Launch VoicePing.
6. Grant **Notification Access** permission when prompted.

---

## 📋 Requirements

| Requirement | Version               |
| ----------- | --------------------- |
| Android OS  | Android 5.0 (API 21+) |
| Permissions | Notification Access   |
| Internet    | Not Required          |

---

## 🔐 Privacy & Security

VoicePing is designed with user privacy in mind.

* OTP codes are never spoken aloud.
* Notification processing happens locally on your device.
* No personal messages are stored.
* No notification content is uploaded to external servers.

---

## 🛠️ Tech Stack

* Kotlin
* Android SDK
* Notification Listener Service
* Android Text-to-Speech (TTS)
* Material Design Components

---

## 📦 Building the Project

### Generate APK

1. Open the project in Android Studio.

2. Navigate to:

   ```
   Build → Build Bundle(s) / APK(s) → Build APK(s)
   ```

3. Wait for the build process to complete.

4. Locate the generated APK:

   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 📌 Release Information

**Current Version:** v1.0.0

### Included Features

* Notification voice announcements
* OTP protection
* Promotional message filtering
* Quiet hours
* Screen-off mode
* Bluetooth-only mode
* App whitelist management
* Duplicate suppression

---

## 👨‍💻 Developer

**Nitesh Kumar**

Built to make notifications accessible, safer, and truly hands-free.
