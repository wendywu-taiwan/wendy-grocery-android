package wendy.grocery.android.cache

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Abstract Base Data source class with error handling
 */
abstract class CacheDataSource {

    private val TAG = CacheDataSource::class.java.simpleName

    protected suspend fun <T> getResult(callback: CacheCallback<T>, get: suspend () -> T)
    = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = get()
            callback.onResult(response)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            callback.onError("Cache call has failed for a following reason:${e.message ?: e.toString()}")
        }
    }


}