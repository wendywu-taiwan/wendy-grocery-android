package wendy.grocery.android.usecase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_product_detail.*
import wendy.grocery.android.R
import wendy.grocery.android.domain.model.Product
import wendy.grocery.android.utilities.extension.observeNavigationEvent
import wendy.grocery.android.view.AmountActionView
import wendy.grocery.android.view.TopBarView

class ProductDetailFragment : androidx.fragment.app.Fragment() {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private val viewModel : ProductViewModel by activityViewModels()

    private lateinit var topBarView: TopBarView
    private lateinit var productName: TextView
    private lateinit var productImage: AppCompatImageView
    private lateinit var amountActionView: AmountActionView
    private lateinit var productPriceTitle: TextView
    private lateinit var productPrice: TextView
    private lateinit var addProductButton: MaterialButton

    /**
     *  Use navigation component safe args to retrieve data passed to fragment
     */
    private val args : ProductDetailFragmentArgs by navArgs()


    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)


        return inflater.inflate(R.layout.fragment_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        observe()
        viewModel.getDetailData(args.productId)
        observeNavigationEvent(viewModel.navigationCommandsLiveEvent, R.id.ProductDetailFragment)
    }

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * Live data to observe
     *
     */
    private fun observe() {
        viewModel.detailDataUpdateLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                updateView(it)
            }
        })
    }


    /**
     * Setup the ui components
     *
     */
    private fun setupView() {
        topBarView = product_detail_header
        productImage = product_detail_image
        amountActionView = detail_amount_view
        productPriceTitle = product_price_title
        productPrice = product_price
        addProductButton = add_product_button

        //todo: apply string
        topBarView.setCloseButtonIcon(R.drawable.ic_arrow_back)
        topBarView.setActionIcon(R.drawable.ic_shopping_cart_black)
        topBarView.showDivider(false)
        topBarView.setOnCloseClickListener{
            viewModel.onClickBack()
        }
        productPriceTitle.text = "Price"
        addProductButton.text = "Add to cart"

        amountActionView.setAmountText("1")
    }

    private fun updateView(product: Product){
        topBarView.setTitle(product.name)
        productPrice.text = product.price

        Glide.with(productImage.context).load(product.image)
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .into(productImage)
    }


    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================


}
