package com.mgkim.libs.webimageview.utils

import android.os.Bundle
import com.mgkim.libs.webimageview.Constants.IS_DEBUG

internal object Log {
    private val TAG = javaClass.simpleName
    // verbose
    fun v(tag: String = TAG, msg: String) {
        if (IS_DEBUG) android.util.Log.v(tag, msg)
    }

    fun v(tag: String = TAG, msg: Int) {
        if (IS_DEBUG) android.util.Log.v(tag, "" + msg)
    }

    fun v(tag: String = TAG, msg: Boolean) {
        if (IS_DEBUG) android.util.Log.v(tag, "" + msg)
    }
    fun v(tag: String = TAG, msg: String, e: Exception) {
        if (IS_DEBUG) android.util.Log.v(tag, msg, e)
    }

    // debug
    fun d(tag: String = TAG, msg: String) {
        if (IS_DEBUG) android.util.Log.d(tag, msg)
    }

    fun d(tag: String = TAG, msg: Int) {
        if (IS_DEBUG) android.util.Log.d(tag, "" + msg)
    }

    fun d(tag: String = TAG, msg: Boolean) {
        if (IS_DEBUG) android.util.Log.d(tag, "" + msg)
    }

    fun d(tag: String = TAG, msg: String, e: Exception) {
        if (IS_DEBUG) android.util.Log.d(tag, msg, e)
    }

    // info
    fun i(tag: String = TAG, msg: String) {
        if (IS_DEBUG) android.util.Log.i(tag, msg)
    }

    fun i(tag: String = TAG, msg: Int) {
        if (IS_DEBUG) android.util.Log.i(tag, "" + msg)
    }

    fun i(tag: String = TAG, msg: Boolean) {
        if (IS_DEBUG) android.util.Log.i(tag, "" + msg)
    }

    fun i(tag: String = TAG, msg: String, e: Exception) {
        if (IS_DEBUG) android.util.Log.i(tag, msg, e)
    }

    // warning
    fun w(tag: String = TAG, msg: String) {
        if (IS_DEBUG) android.util.Log.w(tag, msg)
    }

    fun w(tag: String = TAG, msg: Int) {
        if (IS_DEBUG) android.util.Log.w(tag, "" + msg)
    }

    fun w(tag: String = TAG, msg: Boolean) {
        if (IS_DEBUG) android.util.Log.w(tag, "" + msg)
    }

    fun w(tag: String = TAG, msg: String, e: Exception) {
        if (IS_DEBUG) android.util.Log.w(tag, msg, e)
    }

    // error
    fun e(tag: String = TAG, msg: String) {
        if (IS_DEBUG) android.util.Log.e(tag, msg)
    }

    fun e(tag: String = TAG, msg: Int) {
        if (IS_DEBUG) android.util.Log.e(tag, "" + msg)
    }

    fun e(tag: String = TAG, msg: Boolean) {
        if (IS_DEBUG) android.util.Log.e(tag, "" + msg)
    }

    fun e(tag: String = TAG, msg: String, exception: Exception) {
        if (IS_DEBUG) android.util.Log.e(tag, msg, exception)
    }

    fun bundle(inputData: Bundle?) {
        if (IS_DEBUG && inputData != null) {
            i(TAG, "$$$ input DATA $$$")
            for (key in inputData.keySet()) {
                i(TAG, "key : " + key + " value : " + inputData.get(key)!!.toString())
            }
            i(TAG, "$$$$$$$$$$$$$$$$$$")
        }
    }
}
