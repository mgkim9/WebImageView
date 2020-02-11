package com.mgkim.libs.webimageview

import android.graphics.Bitmap
import android.os.Build

internal object Constants {
    var IS_DEBUG: Boolean = BuildConfig.DEBUG
    private val sConfigs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        arrayOf(
            null,
            Bitmap.Config.ALPHA_8,
            null,
            Bitmap.Config.RGB_565,
            null,//Bitmap.Config.ARGB_4444,
            Bitmap.Config.ARGB_8888,
            Bitmap.Config.RGBA_F16,
            Bitmap.Config.HARDWARE
        ) else
        arrayOf(
            null,
            Bitmap.Config.ALPHA_8,
            null,
            Bitmap.Config.RGB_565,
            null,//Bitmap.Config.ARGB_4444,
            Bitmap.Config.ARGB_8888
        )

    internal fun nativeToConfig(ni: Int): Bitmap.Config {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && ni <= Bitmap.Config.HARDWARE.value) {
            sConfigs[ni] ?: Bitmap.Config.RGB_565
        } else if (ni <= Bitmap.Config.ARGB_8888.value) {
            sConfigs[ni] ?: Bitmap.Config.RGB_565
        } else {
            return Bitmap.Config.RGB_565
        }
    }
}

enum class Method {
    GET, POST
}

internal val Bitmap.Config.value: Int
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            when(this) {
                Bitmap.Config.ALPHA_8 ->1
                Bitmap.Config.RGB_565 -> 3
//                Bitmap.Config.ARGB_4444 -> 4
                Bitmap.Config.ARGB_8888 -> 5
                Bitmap.Config.RGBA_F16 -> 6
                Bitmap.Config.HARDWARE -> 7
                else -> 5
            }
        } else {
            when(this) {
                Bitmap.Config.ALPHA_8 ->1
                Bitmap.Config.RGB_565 -> 3
//                Bitmap.Config.ARGB_4444 -> 4
                Bitmap.Config.ARGB_8888 -> 5
                else -> 5
            }
        }
    }