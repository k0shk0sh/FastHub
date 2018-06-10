package com.fastaccess.github.utils.extensions

import com.fastaccess.data.storage.FastHubSharedPreference

/**
 * Created by Kosh on 10.06.18.
 */

inline var FastHubSharedPreference.token: String?
    get() = this.get("token")
    set(value) = this.set("token", value)

inline var FastHubSharedPreference.enterpriseToken: String?
    get() = this.get("enterprise_token")
    set(value) = this.set("enterprise_token", value)

inline var FastHubSharedPreference.otpCode: String?
    get() = this.get("otp_code")
    set(value) = this.set("otp_code", value)

inline var FastHubSharedPreference.enterpriseOtpCode: String?
    get() = this.get("enterprise_otp_code")
    set(value) = this.set("enterprise_otp_code", value)

inline var FastHubSharedPreference.enterpriseUrl: String?
    get() = this.get("enterprise_url")
    set(value) = this.set("enterprise_url", value)

inline var FastHubSharedPreference.notificationDuration: Int
    get() = this.get("notification_duration", 30)?.times(60) ?: 30.times(60)
    set(value) = this.set("notification_duration", value)

inline var FastHubSharedPreference.backButton: Boolean
    get() = this.get("press_twice_back_button", false) ?: false
    set(value) = this.set("press_twice_back_button", value)

inline var FastHubSharedPreference.isRectAvatar: Boolean
    get() = this.get("rect_avatar", false) ?: false
    set(value) = this.set("rect_avatar", value)

inline var FastHubSharedPreference.markNotificationAsRead: Boolean
    get() = this.get("markNotificationAsRead", false) ?: false
    set(value) = this.set("markNotificationAsRead", value)

inline var FastHubSharedPreference.sentVia: Boolean
    get() = this.get("fasthub_signature", false) ?: false
    set(value) = this.set("fasthub_signature", value)

inline var FastHubSharedPreference.theme: Int
    get() = this.get("appTheme", 1) ?: 1
    set(value) = this.set("appTheme", value)

inline var FastHubSharedPreference.language: String
    get() = this.get("app_language", "en") ?: "en"
    set(value) = this.set("app_language", value)

inline var FastHubSharedPreference.showWhatsNew: Boolean
    get() = this.get("whats_new", false) ?: true
    set(value) = this.set("whats_new", value)

inline var FastHubSharedPreference.notificationSound: Boolean
    get() = this.get("notificationSound", false) ?: false
    set(value) = this.set("notificationSound", value)

inline var FastHubSharedPreference.amlodTheme: Boolean
    get() = this.get("amlod_theme_enabled", false) ?: false
    set(value) = this.set("amlod_theme_enabled", value)

inline var FastHubSharedPreference.bluishTheme: Boolean
    get() = this.get("bluish_theme_enabled", false) ?: false
    set(value) = this.set("bluish_theme_enabled", value)

inline var FastHubSharedPreference.isPro: Boolean
    get() = this.get("fasthub_pro_items", false) ?: false
    set(value) = this.set("fasthub_pro_items", value)

inline var FastHubSharedPreference.isEnterprisePurchased: Boolean
    get() = this.get("enterprise_item", false) ?: false
    set(value) = this.set("enterprise_item", value)

inline var FastHubSharedPreference.tineNavBar: Boolean
    get() = this.get("navigation_color", false) ?: false
    set(value) = this.set("navigation_color", value)

inline val FastHubSharedPreference.allFeatures: Boolean get() = isEnterprise && isPro

inline val FastHubSharedPreference.hasSupported: Boolean get() = isPro || amlodTheme || bluishTheme

inline val FastHubSharedPreference.isEnterprise: Boolean get() = !enterpriseUrl.isNullOrEmpty()

fun FastHubSharedPreference.resetEnterprise() {
    enterpriseUrl = null
    enterpriseToken = null
    enterpriseOtpCode = null
}

