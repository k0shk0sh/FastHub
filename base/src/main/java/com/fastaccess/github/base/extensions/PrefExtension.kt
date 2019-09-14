package com.fastaccess.github.base.extensions

import com.fastaccess.data.storage.FastHubSharedPreference

/**
 * Created by Kosh on 10.06.18.
 */

inline var FastHubSharedPreference.token: String?
    get() = this.getString("token")
    set(value) = this.set("token", value)

inline var FastHubSharedPreference.enterpriseToken: String?
    get() = this.getString("enterprise_token")
    set(value) = this.set("enterprise_token", value)

inline var FastHubSharedPreference.otpCode: String?
    get() = this.getString("otp_code")
    set(value) = this.set("otp_code", value)

inline var FastHubSharedPreference.enterpriseOtpCode: String?
    get() = this.getString("enterprise_otp_code")
    set(value) = this.set("enterprise_otp_code", value)

inline var FastHubSharedPreference.enterpriseUrl: String?
    get() = this.getString("enterprise_url")
    set(value) = this.set("enterprise_url", value)

inline var FastHubSharedPreference.notificationDuration: Int
    get() = this.getInt("notification_duration", 30).times(60)
    set(value) = this.set("notification_duration", value)

inline var FastHubSharedPreference.backButton: Boolean
    get() = this.getBoolean("press_twice_back_button", false)
    set(value) = this.set("press_twice_back_button", value)

inline var FastHubSharedPreference.isRectAvatar: Boolean
    get() = this.getBoolean("rect_avatar", false)
    set(value) = this.set("rect_avatar", value)

inline var FastHubSharedPreference.markNotificationAsRead: Boolean
    get() = this.getBoolean("markNotificationAsRead", false)
    set(value) = this.set("markNotificationAsRead", value)

inline var FastHubSharedPreference.sentVia: Boolean
    get() = this.getBoolean("fasthub_signature", false)
    set(value) = this.set("fasthub_signature", value)

inline var FastHubSharedPreference.theme: Int
    get() = this.getInt("appTheme", 1)
    set(value) = this.set("appTheme", value)

inline var FastHubSharedPreference.language: String
    get() = this.getString("app_language", "en") ?: "en"
    set(value) = this.set("app_language", value)

inline var FastHubSharedPreference.showWhatsNew: Boolean
    get() = this.getBoolean("whats_new", false)
    set(value) = this.set("whats_new", value)

inline var FastHubSharedPreference.notificationSound: Boolean
    get() = this.getBoolean("notificationSound", false)
    set(value) = this.set("notificationSound", value)

inline var FastHubSharedPreference.amlodTheme: Boolean
    get() = this.getBoolean("amlod_theme_enabled", false)
    set(value) = this.set("amlod_theme_enabled", value)

inline var FastHubSharedPreference.bluishTheme: Boolean
    get() = this.getBoolean("bluish_theme_enabled", false)
    set(value) = this.set("bluish_theme_enabled", value)

inline var FastHubSharedPreference.isPro: Boolean
    get() = this.getBoolean("fasthub_pro_items", false)
    set(value) = this.set("fasthub_pro_items", value)

inline var FastHubSharedPreference.isEnterprisePurchased: Boolean
    get() = this.getBoolean("enterprise_item", false)
    set(value) = this.set("enterprise_item", value)

inline var FastHubSharedPreference.tineNavBar: Boolean
    get() = this.getBoolean("navigation_color", false)
    set(value) = this.set("navigation_color", value)

inline val FastHubSharedPreference.allFeatures: Boolean get() = isEnterprise && isPro

inline val FastHubSharedPreference.hasSupported: Boolean get() = isPro || amlodTheme || bluishTheme

inline val FastHubSharedPreference.isEnterprise: Boolean get() = !enterpriseUrl.isNullOrEmpty()

fun FastHubSharedPreference.resetEnterprise() {
    enterpriseUrl = null
    enterpriseToken = null
    enterpriseOtpCode = null
}

