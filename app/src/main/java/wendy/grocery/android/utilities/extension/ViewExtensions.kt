package wendy.grocery.android.utilities.extension

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat


/**
 * Tint drawable set in src for ImageView.
 *
 * @param tintColor color id used for drawable
 */
fun ImageView.tint(@ColorRes tintColor: Int) {
    this.setColorFilter(
            ContextCompat.getColor(this.context, tintColor),
            android.graphics.PorterDuff.Mode.SRC_IN
    )
}

/**
 * Hide keyboard
 *
 */
fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

