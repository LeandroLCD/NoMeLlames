package cl.blipblipcode.prefixsapp.utils

import android.util.Log
import app.cash.turbine.TurbineTestContext

suspend fun <T> TurbineTestContext<T>.awaitMatches(predicate: (T) -> Boolean): T {
    while (true) {
        val item = awaitItem()
        Log.i("TestRunner","awaitMatches item: $item")
        if (predicate(item)) {
            return item
        }
    }
}