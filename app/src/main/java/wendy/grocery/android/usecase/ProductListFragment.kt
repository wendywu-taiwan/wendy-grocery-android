package wendy.grocery.android.usecase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_product_list.*
import wendy.grocery.android.R
import wendy.grocery.android.domain.model.ProductCategory
import wendy.grocery.android.utilities.extension.getLocaleStringResource
import wendy.grocery.android.utilities.extension.observeNavigationEvent
import wendy.grocery.android.view.TopBarView
import java.lang.ref.WeakReference

class ProductListFragment : androidx.fragment.app.Fragment() {

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

        return inflater.inflate(R.layout.fragment_product_list, container, false)
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
        viewModel.listDataUpdateLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                setupAdapter(it)
            }
        })
    }


    /**
     * Setup the ui components
     *
     */
    private fun setupView() {
        topBarView = product_list_header
        dataListView = product_data_list

        topBarView.setTitle(getLocaleStringResource(R.string.product_list_title))
        topBarView.showCloseButton(false)
        topBarView.setActionIcon(R.drawable.ic_shopping_cart_black)
        topBarView.setOnActionClickListener {
            viewModel.onClickListCart()
        }
    }

    /** Initialize the recycler view with configurations */
    private fun initAdapter() {
        controller = ProductListController(
            showCategory = true,
            showAmountAction = false,
            onClickItemListener = viewModel::onClickListProduct,
            null)
        controller?.recyclerView = WeakReference(dataListView)
        dataListView.adapter =controller?.adapter
        dataListView.layoutManager = LinearLayoutManager(requireContext())
    }

    /** Update the list data */
    private fun setupAdapter(dataList: List<ProductCategory>){
        controller?.setData(dataList)
    }


    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================


}
