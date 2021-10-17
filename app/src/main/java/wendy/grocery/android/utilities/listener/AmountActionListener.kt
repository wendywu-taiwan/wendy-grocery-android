package wendy.grocery.android.utilities.listener

/** Listener to interaction event with amount action view */
interface AmountActionListener {
    /** when user click on minus button */
    fun onAmountTextEdit(id: String?, text: String?)
}