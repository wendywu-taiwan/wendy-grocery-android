package wendy.grocery.android.usecase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_product_cart.*
import wendy.grocery.android.R
import wendy.grocery.android.domain.model.ProductCategory
import wendy.grocery.android.utilities.extension.observeNavigationEvent
import wendy.grocery.android.utilities.extension.showToast
import wendy.grocery.android.utilities.extension.toPrice
import wendy.grocery.android.utilities.listener.AmountActionListener
import wendy.grocery.android.utilities.livedata.EventObserver
import wendy.grocery.android.view.TopBarView
import java.lang.ref.WeakReference

class ProductCartFragment : androidx.fragment.app.Fragment() {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private val viewModel : ProductViewModel by activityViewModels()

    private var controller : ProductListController? = null

    private lateinit var topBarView: TopBarView
    private lateinit var dataListView: RecyclerView
    private lateinit var totalPriceTitle: TextView
    private lateinit var totalPriceText: TextView
    private lateinit var buyNowButton: MaterialButton

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_product_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        observe()
        initAdapter()

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
        viewModel.cartDataUpdateLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                setupAdapter(it)
                setBuyButtonEnable(!it.isNullOrEmpty())
            }
        })
        viewModel.totalPriceUpdateLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                totalPriceText.text = it.toString().toPrice()
            }
        })
        viewModel.isCartEmptyLiveData.observe(viewLifecycleOwner, EventObserver {
            setBuyButtonEnable(!it)
        })
    }


    /**
     * Setup the ui components
     *
     */
    private fun setupView() {
        topBarView = product_cart_header
        dataListView = product_cart_list
        totalPriceTitle = total_price_title
        totalPriceText = total_price_text
        buyNowButton = buy_now_button

        topBarView.setTitle(getString(R.string.product_cart_title))
        topBarView.setOnCloseClickListener {
            requireActivity().onBackPressed()
        }
        totalPriceTitle.text = getString(R.string.product_cart_total)
        totalPriceText.text = "$0"
        buyNowButton.text = getString(R.string.product_cart_buy_button)
        buyNowButton.setOnClickListener {
            viewModel.clearCartProducts()
            showToast(getString(R.string.product_cart_buy_message, totalPriceText.text))
        }
    }

    /** Initialize the recycler view with configurations */
    private fun initAdapter() {
        controller = ProductListController(
            showCategory = false,
            showAmountAction = true,
            onClickItemListener = viewModel::onClickCartProduct,
            object : AmountActionListener {
                override fun onAmountTextEdit(id: String?, text: String?) {
                    viewModel.updateCartProductAmount(id, text)
                }
            }

            )
        controller?.recyclerView = WeakReference(dataListView)
        dataListView.adapter =controller?.adapter
        dataListView.layoutManager = LinearLayoutManager(requireContext())
    }

    /** Update the list data */
    private fun setupAdapter(dataList: List<ProductCategory>){
        controller?.setData(dataList)
    }

    /** Enable/disable the buy now button according to the cart products are empty or not */
    private fun setBuyButtonEnable(enable: Boolean){
        buyNowButton.isEnabled = enable
        if(enable){
            buyNowButton.background.setTint(ContextCompat.getColor(requireContext(), R.color.secondary_variants_light))
        }else{
            buyNowButton.background.setTint(ContextCompat.getColor(requireContext(), R.color.gray_3))
        }
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================

}