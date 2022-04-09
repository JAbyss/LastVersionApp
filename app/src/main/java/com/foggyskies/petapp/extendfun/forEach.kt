package com.foggyskies.petapp.extendfun

import androidx.compose.runtime.Composable

public inline fun <T> Iterable<T>.forEachRepeatable(action: (T) -> Unit): Unit {
    while (true)
        for (element in this) action(element)
}
@Composable
public inline fun <T> Iterable<T>.forEachComposable(action: @Composable (T) -> Unit): Unit {
    for (element in this) action(element)
}