package com.supter.data.network

data class Event<out T>(val status: Status, val data: T?, val error: Error?, val message: String?) {

    companion object {
        fun <T> loading(): Event<T> {
            return Event(Status.LOADING, null, null, null)
        }

        fun <T> success(data: T?): Event<T> {
            return Event(Status.SUCCESS, data, null, null)
        }

        fun <T> genericError(message: String?): Event<T> {
            return Event(Status.GENERIC_ERROR, null, null, message)
        }

        fun <T> error(error: Error?): Event<T> {
            return Event(Status.ERROR, null, error, null)
        }
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    GENERIC_ERROR,
    LOADING
}