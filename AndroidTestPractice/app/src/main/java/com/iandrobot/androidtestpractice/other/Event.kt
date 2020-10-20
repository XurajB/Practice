package com.iandrobot.androidtestpractice.other

open class Event<out T>(private val content: T) {
    var hasBeenHandled = false
        private set // Allow external read nut not write

    // returns the content and prevents its use again
    // if the livedata already emitted an event, then orientation changes - it will emit same thing again. to prevent this
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    // return the content, even if it's already been handled
    fun peekContent(): T = content
}