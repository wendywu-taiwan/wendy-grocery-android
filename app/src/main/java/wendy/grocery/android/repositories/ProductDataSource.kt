package wendy.grocery.android.repositories

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import wendy.grocery.android.cache.CacheCallback
import wendy.grocery.android.cache.CacheDataSource
import wendy.grocery.android.cache.CacheFileName
import wendy.grocery.android.domain.manager.AssetsManager
import wendy.grocery.android.domain.manager.CacheManager
import wendy.grocery.android.domain.model.ProductCategory

/** Data source class that interact with the cache manager, asset manager and api manager  */
class ProductDataSource : CacheDataSource() {

    /** get the [ProductCategory] from assets */
    fun getProductCategories(): List<ProductCategory>? {
        val jsonString = AssetsManager.getJsonStringFromAsset(AssetsManager.AssetType.PRODUCT_LIST)
        val type = object : TypeToken<List<ProductCategory>>() {}.type
        return GsonBuilder().create().fromJson(jsonString, type) as List<ProductCategory>?
    }

    /** get the cached cart product */
    suspend fun getCachedCartProducts(
        callback: CacheCallback<List<ProductCategory>?>
    ) = getResult(callback) {
        val jsonString = CacheManager.load(CacheFileName.PRODUCT.fileName)
        val type = object : TypeToken<List<ProductCategory>?>(){}.type
        return@getResult GsonBuilder().create().fromJson(jsonString, type) as List<ProductCategory>?
    }

    /** save the cart products to cache */
    suspend fun saveCachedCartProducts(
        callback: CacheCallback<List<ProductCategory>?>,
        data: List<ProductCategory>?
    ) = getResult(callback) {
       val jsonString = CacheManager.cache(
           CacheFileName.PRODUCT.fileName,
            Gson().toJson(data)
        )
        val type = object : TypeToken<List<ProductCategory>?>(){}.type
        return@getResult GsonBuilder().create().fromJson(jsonString, type) as List<ProductCategory>?
    }
}