package wendy.grocery.android.utilities.listener

/** Listener to interaction event with amount action view */
interface AmountActionListener {
    /** when user click on add button */
    fun onAddButtonClick(id: String?)
    /** when user click on minus button */
    fun onMinusButtonClick(id: String?)
    /** when user click on minus button */
    fun onAmountTextEdit(id: String?, text: String)
}