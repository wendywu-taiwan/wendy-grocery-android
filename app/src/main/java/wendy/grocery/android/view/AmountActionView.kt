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
class AmountActionView (context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    // ===========================================================
    // Fields
    // ===========================================================

    private val addIconButton: ConstraintLayout
    private val minusIconButton: ConstraintLayout
    private val amountText: EditText

    private var id: String? = null

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

    fun setId(id: String){
        this.id = id
    }

    fun setAmountText(amount: String?){
        amountText.setText(amount)
    }

    fun setAmountActionListener(listener: AmountActionListener?){
        if(listener == null) return

        addIconButton.setOnClickListener {
            listener.onAddButtonClick(id)
        }
        minusIconButton.setOnClickListener {
            listener.onMinusButtonClick(id)
        }
        amountText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                listener.onAmountTextEdit(id, s.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                listener.onAmountTextEdit(id, s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                listener.onAmountTextEdit(id, s.toString())
            }
        })
    }

}