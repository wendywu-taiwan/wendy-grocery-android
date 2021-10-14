package wendy.grocery.android.repositories

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import wendy.grocery.android.domain.manager.AssetsManager
import wendy.grocery.android.domain.model.ProductCategory

class ProductDataSource {

    fun getProductCategories(): List<ProductCategory>? {
        val jsonString = AssetsManager.getJsonStringFromAsset(AssetsManager.AssetType.PRODUCT_LIST)
        val type = object : TypeToken<List<ProductCategory>>() {}.type
        return GsonBuilder().create().fromJson(jsonString, type) as List<ProductCategory>?
    }

    suspend fun getCachedCartProducts(): List<ProductCategory>?{
        return null
    }

    suspend fun saveCachedCartProducts(): Boolean {
        return true
    }
}