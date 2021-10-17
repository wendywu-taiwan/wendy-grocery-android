package wendy.grocery.android.cache

/** interface that handle the success and fail condition when interacting with cache */
interface CacheCallback<T> {
    fun onResult(data: T?)
    fun onError(message: String)
}