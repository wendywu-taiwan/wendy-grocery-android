package wendy.grocery.android.domain.model

/**
 * Class for holding the product information
 *
 * @param name the display name of the product
 * @param price the price of the product
 * @param image the url of the image resource
 * */
data class Product(
        var name: String,
        var price: String,
        var image: String
)
