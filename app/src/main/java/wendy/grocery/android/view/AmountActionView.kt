package wendy.grocery.android.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import wendy.grocery.android.R
import wendy.grocery.android.utilities.listener.AmountActionListener

/** Custom view for amount adding and removing */
class AmountActionView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    companion object{
        const val MAX_AMOUNT = 99  // maximum amount that the user can set
        const val MIN_AMOUNT = 0   // minimum amount that the user can set
        const val RESET_AMOUNT = 0  // if the user set an invalid input, reset it
    }

    // ===========================================================
    // Fields
    // ===========================================================

    private val TAG = AmountActionView::class.java.simpleName

    private val addIconButton: ConstraintLayout
    private val minusIconButton: ConstraintLayout
    private val amountText: EditText

    /** the product id */
    private var productId: String? = null

    /** the current amount in the amountText */
    private var currentAmountText: String? = null

    private var listener: AmountActionListener? = null

    // ===========================================================
    // Init
    // ===========================================================

    init {
        LayoutInflater.from(context).inflate(R.layout.view_amount_action, this, true)

        addIconButton = findViewById(R.id.layout_amount_add)
        minusIconButton = findViewById(R.id.layout_amount_minus)
        amountText = findViewById(R.id.product_amount_text)
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** set the product id to custom view */
    fun setProductId(id: String) {
        productId = id
    }

    /** return the product id set to this view */
    fun getProductId(): String? {
        return this.productId
    }

    /** set the amount text to edit text */
    fun setAmountText(amount: String?) {
        currentAmountText = amount
        amountText.setText(amount)
        val pos: Int = amountText.text.length
        amountText.setSelection(pos)
    }

    /** return the amount text string */
    fun getAmountText(): String? {
        return this.currentAmountText
    }

    /** set the listener for [addIconButton], [minusIconButton], [amountText] */
    fun setAmountActionListener(listener: AmountActionListener?){
        this.listener = listener

        addIconButton.setOnClickListener {
           onClickAddAmount()
        }
        minusIconButton.setOnClickListener {
           onClickMinusAmount()
        }
        amountText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onEditAmountText(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                onEditAmountText(s.toString())
            }
        })
    }

    /** Add 1 to current amount and check if it over the max amount */
    private fun onClickAddAmount(){
        currentAmountText?.let {
            updateAmount(it.toInt() + 1)
        }?: kotlin.run {
            updateAmount(1)
        }
    }

    /** Minus 1 from current amount and check if it smaller than the min amount */
    private fun onClickMinusAmount(){
        currentAmountText?.let {
            updateAmount(it.toInt() - 1)
        }?: kotlin.run {
            updateAmount(RESET_AMOUNT)
        }
    }

    /** Edit current amount and check if it over the max amount or smaller than the min amount */
    private fun onEditAmountText(amount: String){
        updateAmount(amount)
    }

    /**
     * Check the condition with given amount
     *
     * @param amount the new amount after the user taking action
     * @param updateEditText if function trigger by add button or minus button, set it to true
     * */
    private fun updateAmount(amount: Int, updateEditText: Boolean = true){
        when {
            amount > MAX_AMOUNT -> {
                currentAmountText = MAX_AMOUNT.toString()
                setAmountText(currentAmountText)
            }
            amount < MIN_AMOUNT -> {
                currentAmountText = MIN_AMOUNT.toString()
                setAmountText(currentAmountText)
            }
            // if amount == currentAmountText means it made by multiple 0, ex. 00000
            updateEditText || amount == currentAmountText?.toInt() -> {
                currentAmountText = amount.toString()
                setAmountText(currentAmountText)
            }
            else -> { // don't update the edit text if the amount is same as current text
                currentAmountText = amount.toString()
            }
        }
        listener?.onAmountTextEdit(productId, currentAmountText)
    }

    /** Update current amount text and callback to listener */
    private fun updateAmount(amount: String){
        if(amount == currentAmountText) return

        if(amount.trim().isBlank())
            updateAmount(RESET_AMOUNT, true)
        else
            updateAmount(amount.toInt(), false)
    }
}