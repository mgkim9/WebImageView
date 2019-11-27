package com.mgkim.libs.webimageview.utils

import android.os.Looper

internal object AssertUtil {

    /**
     * Throws an [java.lang.IllegalArgumentException] if called on a thread other than the main
     * thread.
     */
    fun assertMainThread() {
        require(isOnMainThread()) { "You must call this method on the main thread" }
    }

    /** Throws an [java.lang.IllegalArgumentException] if called on the main thread.  */
    fun assertBackgroundThread() {
        require(isOnBackgroundThread()) { "You must call this method on a background thread" }
    }

    /** Returns `true` if called on the main thread, `false` otherwise.  */
    fun isOnMainThread(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper()
    }

    /** Returns `true` if called on a background thread, `false` otherwise.  */
    fun isOnBackgroundThread(): Boolean {
        return !isOnMainThread()
    }
}