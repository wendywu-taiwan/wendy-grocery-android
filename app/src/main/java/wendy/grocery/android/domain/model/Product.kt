package wendy.grocery.android.domain.model

import android.util.Log

/**
 * Class for holding the product information
 *
 * @param name the display name of the product
 * @param price the price of the product
 * @param image the url of the image resource
 * */
data class Product(
        var id: String,
        var name: String,
        var price: String,
        var image: String
){
        private var amount: Int = 0

        override fun toString(): String {
                val string = "id:$id name:$name price:$price image:$image amount:$amount"
                Log.d("Product", string)
                return string
        }

        /** Check if product is in the cart */
        fun isCartProduct(): Boolean {
                return amount > 0
        }

        /** Set the amount of product */
        fun setAmount(amount: Int) {
                this.amount = amount
        }

        /** Get the amount of product */
        fun getAmount(): Int {
                return amount
        }

}
