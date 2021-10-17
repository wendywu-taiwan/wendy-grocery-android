package wendy.grocery.android.usecase

import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import wendy.grocery.android.domain.model.ProductCategory
import wendy.grocery.android.utilities.extension.toPrice
import wendy.grocery.android.utilities.listener.AmountActionListener
import wendy.grocery.android.view.decoration.ProductListDecoration
import java.lang.ref.WeakReference

/** The controller to build epoxy model with product data */
class ProductListController(private val showCategory: Boolean = true,
                             private val showAmountAction: Boolean = true,
                            private val onClickItemListener: ((String) -> Unit)? = null,
                             private val amountActionListener: AmountActionListener? = null,
) : TypedEpoxyController<List<ProductCategory>>(){

    /** Keep a weak reference of recyclerview for adding item decoration after setData */
    lateinit var recyclerView: WeakReference<RecyclerView>

    override fun buildModels(list: List<ProductCategory>) {
        // to display title or not according to the item index and showCategory settings
        var showTitle = false
        // to display the space under the item or not
        var showTitleTopSpace = false

        list.forEachIndexed { index, category ->
            if(showCategory)
                showTitle = true

            if(index != 0)
                showTitleTopSpace = true

            category.productList.forEach { data ->
                val price = "\$${data.price}"
                product {
                    id(data.id)
                    productId(data.id)
                    showTitleTopSpace(showTitleTopSpace)
                    titleText(category.name)
                    showTitle(showTitle)
                    image(data.image)
                    name(data.name)
                    price(data.price.toPrice())
                    amount(data.getAmount().toString())
                    showAmountActionView(showAmountAction)
                    itemClickListener(onClickItemListener)
                    amountActionListener(amountActionListener)
                }
                showTitle = false
                showTitleTopSpace = false
            }
        }

        if (!list.isNullOrEmpty() && recyclerView.get()?.itemDecorationCount == 0) {
            recyclerView.get()!!.addItemDecoration(ProductListDecoration())
        }
    }
}