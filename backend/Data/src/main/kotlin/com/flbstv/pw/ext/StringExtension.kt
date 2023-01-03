package com.flbstv.pw.ext

fun String.limit(maxSize: Int): String {
    return this.substring(0, this.length.coerceAtMost(maxSize));
}