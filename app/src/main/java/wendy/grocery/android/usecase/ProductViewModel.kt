package wendy.grocery.android.usecase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import wendy.grocery.android.domain.model.Product
import wendy.grocery.android.domain.model.ProductCategory
import wendy.grocery.android.repositories.ProductDataSource
import wendy.grocery.android.utilities.listener.AmountActionListener
import wendy.grocery.android.utilities.navigation.NavigationCommand
import java.util.concurrent.ConcurrentHashMap

class ProductViewModel : ViewModel() {
    companion object{
        const val MAX_AMOUNT = 99
    }

    // ===========================================================
    // Constants
    // ===========================================================

    private val TAG = ProductViewModel::class.java.simpleName

    private val dataSource = ProductDataSource()

    // ===========================================================
    // Fields
    // ===========================================================

    /** Navigation observables */
    private val navigationCommandsSLE : MutableLiveData<NavigationCommand> by lazy { MutableLiveData<NavigationCommand>() }
    val navigationCommandsLiveEvent: LiveData<NavigationCommand> by lazy { navigationCommandsSLE }

    /** Notify the view that detail data has updated */
    private val detailDataUpdateMutableLiveData : MutableLiveData<Product?> by lazy { MutableLiveData<Product?>() }
    val detailDataUpdateLiveData: LiveData<Product?> by lazy { detailDataUpdateMutableLiveData }

    /** Notify the view that list data has updated */
    private val listDataUpdateMutableLiveData : MutableLiveData<List<ProductCategory>?> by lazy { MutableLiveData<List<ProductCategory>?>() }
    val listDataUpdateLiveData: LiveData<List<ProductCategory>?> by lazy { listDataUpdateMutableLiveData }

    /** Notify the view that cart data has updated */
    private val cartDataUpdateMutableLiveData : MutableLiveData<List<ProductCategory>?> by lazy { MutableLiveData<List<ProductCategory>?>() }
    val cartDataUpdateLiveData: LiveData<List<ProductCategory>?> by lazy { cartDataUpdateMutableLiveData }


    /** map that keep the product id and product instance mapping */
    private val productsIdMap: ConcurrentHashMap<String, Product> = ConcurrentHashMap()

    private var originalData: List<ProductCategory>? = ArrayList()

    init {
        loadProductCategories()
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * Receive on click event on back button
     */
    fun onClickBack() {
        navigationCommandsSLE.value = NavigationCommand.Back
    }

    /** When user clicking on list product item, navigate to product detail page */
    fun onClickListProduct(id: String){
        Log.d(TAG,"onClickListProduct:$id")
        navigationCommandsSLE.value =
            NavigationCommand.To(
                ProductListFragmentDirections.actionProductListFragmentToProductDetailFragment(id)
            )
    }

    /** When user clicking on cart icon, navigate to product cart page */
    fun onClickListCart(){
        navigationCommandsSLE.value =
            NavigationCommand.To(
                ProductListFragmentDirections.actionProductListFragmentToProductCartFragment()
            )
    }

    /** When user clicking on cart product item, navigate to product detail page */
    fun onClickCartProduct(id: String){
        navigationCommandsSLE.value =
            NavigationCommand.To(
                ProductCartFragmentDirections.actionProductCartFragmentToProductDetailFragment(id)
            )
    }

    fun getDetailData(id: String){
        val product = productsIdMap[id] ?: return
        detailDataUpdateMutableLiveData.value = product
    }


    fun updateProductCartData() = viewModelScope.launch{
        val cartCategories = ArrayList<ProductCategory>()
        withContext(Dispatchers.Default) {
            originalData?.forEach lit@{ category ->
                if (!category.hasCartProduct())
                    return@lit

                val copiedCategory = category.copy(name = category.name, productList = ArrayList())
                val cartProducts = category.productList.filter { it.isCartProduct() }
                copiedCategory.productList = cartProducts
                cartCategories.add(copiedCategory)
            }
        }
        listDataUpdateMutableLiveData.value = cartCategories
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /** Get product category list from data source and apply to UI */
    private fun loadProductCategories() = viewModelScope.launch {
        val categories = withContext(Dispatchers.IO){
            val list = dataSource.getProductCategories()
            list?.forEach { category ->
                category.productList.forEach {
                    productsIdMap[it.id] = it
                }
            }
            return@withContext list
        }
        Log.d(TAG, "getProductCategories size:${categories?.size}")
        originalData = categories?.toList()
        listDataUpdateMutableLiveData.value = categories
    }

    /** When the user click on add 1 item to the cart, update data and UI */
    private fun addProductAmount(id: String, add: Int){
        val product = productsIdMap[id] ?: return
        var newAmount = product.getAmount() + add

        if(newAmount > MAX_AMOUNT)
            newAmount = MAX_AMOUNT

        product.setAmount(newAmount)
        updateProductCartData()
    }

    /** When the user click on minus 1 item from the cart, update data and UI */
    private fun minusProductAmount(id: String){
        val product = productsIdMap[id] ?: return
        if(product.getAmount() == 0) return

        product.setAmount(product.getAmount() - 1)
        updateProductCartData()
    }

    /** When the user modify the amount of the product in the cart, update data and UI */
    private fun setProductAmount(id: String, amount: String){
        val product = productsIdMap[id] ?: return

        var newAmount = amount.toInt()

        if(newAmount > MAX_AMOUNT)
            newAmount = MAX_AMOUNT

        product.setAmount(newAmount)
        updateProductCartData()
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================

    inner class DetailActionListener: AmountActionListener {
        override fun onAddButtonClick(id: String?) {
            TODO("Not yet implemented")
        }

        override fun onMinusButtonClick(id: String?) {
            TODO("Not yet implemented")
        }

        override fun onAmountTextEdit(id: String?, text: String) {
            TODO("Not yet implemented")
        }

    }

}