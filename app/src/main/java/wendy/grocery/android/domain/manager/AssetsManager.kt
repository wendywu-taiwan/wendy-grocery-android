package wendy.grocery.android.domain.manager

import wendy.grocery.android.application.WendyGroceryApplication
import java.io.IOException

object AssetsManager {
    /** Load the asset file json content with given asset type  */
    fun getJsonStringFromAsset(assetType: AssetType): String? {
        val assets = WendyGroceryApplication.getInstance().assets
        val jsonString: String
        try {
            jsonString = assets.open(assetType.name).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

    /** Define the json file type and file path mapping */
    enum class AssetType(filePath: String) {
        PRODUCT_LIST("products.json")
    }

}