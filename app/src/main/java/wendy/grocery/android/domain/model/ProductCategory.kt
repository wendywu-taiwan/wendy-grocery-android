package wendy.grocery.android.domain.model

/**
 * Class for holding the category information
 *
 * @param name the display name of the category
 * @param productList the products that belong to this category
 * */
data class ProductCategory (
        var name: String,
        var productList: List<Product> = ArrayList()
)