package com.ahmedzenhom.ebbinghaus.data.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseRepository {

    suspend fun <T> execute(job: suspend () -> T): T = withContext(Dispatchers.IO) {
        try {
            return@withContext job()
        } catch (e: Exception) {
            Log.e("BaseRepository", "Error: " + e.message, e)
            throw e
        }
    }
}