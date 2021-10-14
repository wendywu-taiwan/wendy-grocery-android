package wendy.grocery.android.utilities.extension

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

