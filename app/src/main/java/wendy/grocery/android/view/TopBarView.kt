package wendy.grocery.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import wendy.grocery.android.R
import wendy.grocery.android.utilities.extension.tint

class TopBarView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    // ===========================================================
    // Fields
    // ===========================================================

    private val title: TextView
    private val actionIconButton: ImageView
    private val closeButton: ImageView
    private val divider: View

    private var customCloseButtonRes: Int? = null // res for close icon
    private var customActionButtonRes: Int? = null // res for action icon

    // ===========================================================
    // Init
    // ===========================================================

    init {
        LayoutInflater.from(context).inflate(R.layout.view_top_bar, this, true)

        title = findViewById(R.id.top_bar_title)
        closeButton = findViewById(R.id.top_bar_close_button)
        actionIconButton = findViewById(R.id.top_bar_right_action_icon)
        divider = findViewById(R.id.top_bar_bottom_divider)
        setupView()
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    fun setTitle(value: String): TopBarView {
        title.text = value
        return this
    }

    /**
     * set title color
     *
     * @param color the color you want to show
     */
    fun setTitleColor(color: Int): TopBarView {
        title.setTextColor(ContextCompat.getColor(context, color))
        return this
    }

    /**
     * set custom icon for close button on LeftTop
     *
     * @param res the resource that you want to show in close icon
     */
    fun setCloseButtonIcon(res: Int?): TopBarView {
        customCloseButtonRes = res
        setupView()
        return this
    }

    /**
     * to show or hide the close button
     *
     * @param show
     */
    fun showCloseButton(show: Boolean){
        if(show){
            closeButton.visibility = View.VISIBLE
        } else {
            closeButton.visibility = View.INVISIBLE
        }
    }

    /**
     * set close button color tint
     *
     * @param color the color that you want to display
     */
    fun setCloseButtonTint(color:Int):TopBarView {
        closeButton.tint(color)
        return this
    }

    /**
     * Set action icon button show/hide
     *
     * @param show true: show, false: hide
     */
    fun setActionIconShow(show: Boolean): TopBarView {
        actionIconButton.visibility = if (show) View.VISIBLE else View.INVISIBLE
        return this
    }

    fun setOnCloseClickListener(close: () -> Unit): TopBarView {
        closeButton.setOnClickListener {
            close.invoke()
        }
        return this
    }

    /**
     * set action click listener,
     * cause action has two different button,
     * so if [customActionButtonRes] is not null, it's mean we are using the image icon action -> [actionIconButton],
     * otherwise we are using text icon action -> [actionButton]
     */
    fun setOnActionClickListener(action: () -> Unit): TopBarView {
        actionIconButton.setOnClickListener {
            action.invoke()
        }
        return this
    }

    /**
     * set icon for action icon button
     *
     * @param drawable the drawable you want to display
     */
    fun setActionIcon(@DrawableRes drawable:Int?): TopBarView {
        customActionButtonRes = drawable
        setupView()
        return this
    }

    /**
     * set action icon button color tint
     *
     * @param color the color that you want to display
     */
    fun setActionIconTint(color:Int):TopBarView{
        actionIconButton.tint(color)
        return this
    }

    /**
     * to show or hide the bottom divider
     *
     * @param show
     */
    fun showDivider(show: Boolean){
        if(show){
            divider.visibility = View.VISIBLE
        } else {
            divider.visibility = View.INVISIBLE
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private fun setupView() {
        customCloseButtonRes?.let { res ->
            closeButton.setImageResource(res)
        }

        customActionButtonRes?.let { res ->
            actionIconButton.visibility = View.VISIBLE
            actionIconButton.setImageResource(res)
        } ?: kotlin.run {
            actionIconButton.visibility = View.INVISIBLE
        }
    }

}