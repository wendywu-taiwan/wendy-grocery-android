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
){
        /** Check if any product in this category is in the cart */
        fun hasCartProduct(): Boolean {
                for(product in productList){
                        if(product.isCartProduct())
                                return true
                }
                return false
        }
}