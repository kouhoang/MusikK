package com.kouhoang.music.base.service

import android.database.sqlite.SQLiteException
import com.bumptech.glide.load.HttpException
import com.kouhoang.music.data.common.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.CancellationException

open class BaseService {

    suspend fun <T> safeCallApi(call: suspend () -> T): Response<T> {
        return try {
            val response = call()
            Response.Success(response)
        } catch (e: HttpException) {
            Response.Error(e.message ?: "Something went wrong")
        } catch (e: IOException) {
            Response.Error(e.message!!)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Response.Error(e.message ?: "Something went wrong")
        } catch (e: SocketTimeoutException) {
            Response.Error(e.message ?: "Something went wrong")
        }
    }

    suspend fun <T> safeCallDao(call: suspend () -> T): Response<T> {
        return try {
            val response = call()
            Response.Success(response)
        } catch (e: SQLiteException) {
            val message = e.message.toString()
            Response.Error(message)
        }
    }
}