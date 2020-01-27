package net.edgwbs.bookstorage.utils

import android.os.Looper.getMainLooper

fun isCurrent(): Boolean {
    return Thread.currentThread() == getMainLooper().thread
}