package wendy.grocery.android.utilities.extension

/**
 * Add price mark before the string
 *
 */
fun String.toPrice(): String{
    return "\$${this}"
}
