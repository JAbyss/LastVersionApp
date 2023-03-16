package com.foggyskies.testingscrollcompose.extendfun

import androidx.compose.runtime.Composable
import kotlinx.coroutines.delay
import kotlin.coroutines.suspendCoroutine

@Composable
fun <K, V> Map<K, V>.forEachKeys(action: @Composable (key: K, value: V, index: Int) -> Unit) {
    for (key in this.keys) action(key, this[key]!!, this.keys.indexOf(key))
}

fun <K, V> Map<K, V>.forEachKeysNotCompose(action: (key: K, value: V, index: Int) -> Unit) {
        for (key in this.keys) action(key, this[key]!!, this.keys.indexOf(key))
}