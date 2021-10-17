package wendy.grocery.android.usecase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_product_detail.*
import wendy.grocery.android.R
import wendy.grocery.android.domain.model.Product
import wendy.grocery.android.utilities.extension.observeNavigationEvent
import wendy.grocery.android.utilities.extension.showToast
import wendy.grocery.android.utilities.extension.toPrice
import wendy.grocery.android.utilities.listener.AmountActionListener
import wendy.grocery.android.utilities.livedata.EventObserver
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
        observeNavigationEvent(viewModel.navigationCommandsLiveEvent)
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
        viewModel.canAddToCartLiveData.observe(viewLifecycleOwner, EventObserver {
            addProductButton.isEnabled =  it
            if(it){
                addProductButton.background.setTint(ContextCompat.getColor(requireContext(), R.color.secondary_variants_light))
            }else{
                addProductButton.background.setTint(ContextCompat.getColor(requireContext(), R.color.gray_3))
            }
        })
        viewModel.isCartEmptyLiveData.observe(viewLifecycleOwner, EventObserver {
            updateCartIcon(it)
        })
        viewModel.showToastLiveData.observe(viewLifecycleOwner, EventObserver {
            val stringRes = it.first
            val parameter = it.second

            if(stringRes == R.string.product_detail_add_message)
                showToast(getString(stringRes, parameter))
            else
                showToast(getString(stringRes))
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

        topBarView.setCloseButtonIcon(R.drawable.ic_arrow_back)
        topBarView.setActionIcon(R.drawable.ic_shopping_cart_black)
        topBarView.showDivider(false)
        topBarView.setOnCloseClickListener{
            requireActivity().onBackPressed()
        }
        topBarView.setOnActionClickListener {
            viewModel.onClickDetailCart()
        }
        updateCartIcon(viewModel.isCartEmpty())

        productPriceTitle.text = getString(R.string.product_detail_title)
        addProductButton.text = getString(R.string.product_detail_add_button)

        amountActionView.setAmountText("1")
        amountActionView.setAmountActionListener(object: AmountActionListener{
            override fun onAmountTextEdit(id: String?, text: String?) {
                viewModel.updateDetailProductAmount(text)
            }

        })

        addProductButton.setOnClickListener {
            val amountText = amountActionView.getAmountText()
            viewModel.onClickAddToCart(amountActionView.getProductId(), amountText)
        }
    }

    private fun updateView(product: Product){
        amountActionView.setProductId(product.id)
        topBarView.setTitle(product.name)
        productPrice.text = product.price.toPrice()

        Glide.with(productImage.context).load(product.image)
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .into(productImage)
    }

    /** Update right top cart icon color according it is empty or not */
    private fun updateCartIcon(empty: Boolean){
        if(empty){
            topBarView.setActionIconTint(R.color.primary_default)
        }else{
            topBarView.setActionIconTint(R.color.secondary_variants_light)
        }
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================


}
