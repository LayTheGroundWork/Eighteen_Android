package com.eighteen.eighteenandroid.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch

fun <T> Flow<T>.collectOnce(coroutineScope: CoroutineScope, collect: (T) -> Unit) {
    coroutineScope.launch {
        this@collectOnce.cancellable().collect {
            collect(it)
            this.cancel()
        }
    }
}