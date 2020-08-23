package com.example.supter.internal

import kotlinx.coroutines.*

fun <T> lazyDeferred(block: suspend CoroutineScope.() -> T): Lazy<Deferred<T>> {
    return lazy {

        //Starting the work during initialisation-time
        GlobalScope.async(start = CoroutineStart.LAZY) {

            //Invoke the work during runtime
            block.invoke(this)
        }
    }
}