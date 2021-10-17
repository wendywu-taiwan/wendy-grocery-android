package wendy.grocery.android.usecase

import android.view.View
import android.widget.ImageView
import android.widget.Space
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import wendy.grocery.android.R
import wendy.grocery.android.utilities.listener.AmountActionListener
import wendy.grocery.android.view.AmountActionView

/**
 * Epoxy model for a product item
 *
 */
@EpoxyModelClass(layout = R.layout.item_product)
abstract class ProductEpoxyModel: EpoxyModelWithHolder<ProductEpoxyModel.ViewHolder>() {

    @EpoxyAttribute
    var showTitleTopSpace: Boolean = false

    @EpoxyAttribute
    lateinit var productId: String

    @EpoxyAttribute
    var titleText: String? = null

    @EpoxyAttribute
    var showTitle: Boolean = false

    @EpoxyAttribute
    var image: String? = null

    @EpoxyAttribute
    var name: String? = null

    @EpoxyAttribute
    var price: String? = null

    @EpoxyAttribute
    var amount: String? = null

    @EpoxyAttribute
    var showAmountActionView: Boolean = true

    @EpoxyAttribute
    var itemClickListener: ((String) -> Unit)? = null

    @EpoxyAttribute
    var amountActionListener: AmountActionListener ? = null

    override fun bind(holder: ViewHolder) {
        super.bind(holder)

        if(showTitleTopSpace)
            holder.titleTopSpace.visibility = View.VISIBLE
        else
            holder.titleTopSpace.visibility = View.GONE

        if(showTitle)
            holder.titleLayout.visibility = View.VISIBLE
        else
            holder.titleLayout.visibility = View.GONE

        holder.titleText.text = titleText

        Glide.with(holder.productImage.context).load(image)
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .into(holder.productImage)

        holder.productName.text = name

        holder.productPrice.text = price

//        holder.contentLayout.setOnClickListener {
//            itemClickListener?.invoke(productId)
//        }

        setAmountActionView(holder.amountActionView)
    }

    private fun setAmountActionView(amountActionView: AmountActionView){
        amountActionView.setId(productId)
        amountActionView.setAmountText(amount)
        amountActionView.setAmountActionListener(listener = amountActionListener)

        if(showAmountActionView)
            amountActionView.visibility = View.VISIBLE
        else
            amountActionView.visibility = View.GONE
    }

    class ViewHolder : EpoxyHolder() {
        lateinit var view: View
        lateinit var titleTopSpace: Space
        lateinit var titleLayout: ConstraintLayout
        lateinit var contentLayout: ConstraintLayout
        lateinit var titleText: TextView
        lateinit var productImage: ImageView
        lateinit var productName: TextView
        lateinit var productPrice: TextView
        lateinit var amountActionView: AmountActionView



        override fun bindView(itemView: View) {
            view = itemView
            titleTopSpace = itemView.findViewById(R.id.title_top_space)
            titleLayout = itemView.findViewById(R.id.product_title_layout)
            contentLayout = itemView.findViewById(R.id.product_content_text_layout)
            titleText = itemView.findViewById(R.id.product_title_text)
            productImage = itemView.findViewById(R.id.product_content_image)
            productName = itemView.findViewById(R.id.product_content_name)
            productPrice = itemView.findViewById(R.id.product_content_price)
            amountActionView = itemView.findViewById(R.id.product_amount_view)
        }
    }

}