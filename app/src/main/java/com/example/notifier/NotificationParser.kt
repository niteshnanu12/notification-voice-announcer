package com.example.notifier

import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.service.notification.StatusBarNotification

object NotificationParser {

    fun parse(context: Context, sbn: StatusBarNotification): String? {
        val packageName = sbn.packageName
        val notification = sbn.notification
        val extras = notification.extras

        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()?.trim()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()?.trim()
        val category = notification.category

        val appLabel = getAppLabel(context, packageName)

        // OTP filter: speak only 'OTP message from [AppName]', not the digits
        if (isOtp(title, text)) {
            return "OTP message from $appLabel"
        }

        // Custom smart phrases based on app
        return when {
            packageName.startsWith("com.whatsapp") -> {
                if (title.isNullOrBlank() || title.equals("WhatsApp", ignoreCase = true)) {
                    null
                } else if (text != null && (text.contains("messages from", ignoreCase = true) || 
                                          text.contains("new message", ignoreCase = true) ||
                                          text.contains("checking for new messages", ignoreCase = true))) {
                    null
                } else {
                    "WhatsApp message from $title"
                }
            }
            packageName == "org.telegram.messenger" || packageName == "org.telegram.plus" -> {
                if (!title.isNullOrEmpty()) {
                    "Telegram message from $title"
                } else {
                    "Telegram message"
                }
            }
            packageName == "com.google.android.gm" -> {
                if (!title.isNullOrEmpty()) {
                    "Gmail message from $title"
                } else {
                    "New email in Gmail"
                }
            }
            category == Notification.CATEGORY_PROMO || 
            category == Notification.CATEGORY_RECOMMENDATION ||
            isPromotionalPackage(packageName) -> {
                "Promotional message from $appLabel"
            }
            packageName == "com.google.android.apps.messaging" || packageName == "com.android.mms" -> {
                if (!title.isNullOrEmpty()) {
                    "Message from $title"
                } else {
                    "Message from $appLabel"
                }
            }
            else -> {
                "$appLabel message from $title"
            }
        }
    }

    private fun getAppLabel(context: Context, packageName: String): String {
        val pm = context.packageManager
        return try {
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName.split(".").lastOrNull() ?: packageName
        }
    }

    private fun isPromotionalPackage(packageName: String): Boolean {
        val lower = packageName.lowercase()
        return lower.contains("myntra") || 
               lower.contains("flipkart") || 
               lower.contains("amazon.mshop") || 
               lower.contains("meesho") || 
               lower.contains("nykaa") || 
               lower.contains("ajio")
    }

    private fun isOtp(title: String?, text: String?): Boolean {
        val combined = "${title ?: ""} ${text ?: ""}".lowercase()
        
        // Match common OTP/verification keywords
        val hasOtpKeyword = combined.contains("otp") || 
                            combined.contains("one-time password") || 
                            combined.contains("verification code") || 
                            combined.contains("security code") || 
                            combined.contains("verification pin") ||
                            combined.contains("activation code") ||
                            combined.contains("code:") ||
                            combined.contains("code is")

        // Match digit sequences of 4 to 8 digits
        val hasDigits = Regex("\\b\\d{4,8}\\b").containsMatchIn(combined)
        
        return hasOtpKeyword && hasDigits
    }
}
