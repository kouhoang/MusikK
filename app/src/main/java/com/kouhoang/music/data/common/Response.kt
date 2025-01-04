package com.kouhoang.music.data.common

sealed class Response<out T>(
    val data: T? = null,
    val message: String? = null
) {

    class Success<T>(data: T) : Response<T>(data = data)

    class Error(message: String) : Response<Nothing>(message = message)
}