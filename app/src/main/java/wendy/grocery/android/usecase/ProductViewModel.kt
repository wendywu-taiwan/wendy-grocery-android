package wendy.grocery.android.usecase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import wendy.grocery.android.R
import wendy.grocery.android.cache.CacheCallback
import wendy.grocery.android.domain.model.Product
import wendy.grocery.android.domain.model.ProductCategory
import wendy.grocery.android.repositories.ProductDataSource
import wendy.grocery.android.utilities.livedata.Event
import wendy.grocery.android.utilities.navigation.NavigationCommand
import wendy.grocery.android.view.AmountActionView
import java.util.concurrent.ConcurrentHashMap

class ProductViewModel : ViewModel() {

    // ===========================================================
    // Constants
    // ===========================================================

    private val TAG = ProductViewModel::class.java.simpleName

    private val dataSource = ProductDataSource()

    // ===========================================================
    // Fields
    // ===========================================================

    /** Navigation observables */
    private val navigationCommandsSLE : MutableLiveData<Event<NavigationCommand>> by lazy { MutableLiveData<Event<NavigationCommand>>() }
    val navigationCommandsLiveEvent: LiveData<Event<NavigationCommand>> by lazy { navigationCommandsSLE }

    /** Notify the view that detail data has updated */
    private val detailDataUpdateMutableLiveData : MutableLiveData<Product?> by lazy { MutableLiveData<Product?>() }
    val detailDataUpdateLiveData: LiveData<Product?> by lazy { detailDataUpdateMutableLiveData }

    /** Notify the view that list data has updated */
    private val listDataUpdateMutableLiveData : MutableLiveData<List<ProductCategory>?> by lazy { MutableLiveData<List<ProductCategory>?>() }
    val listDataUpdateLiveData: LiveData<List<ProductCategory>?> by lazy { listDataUpdateMutableLiveData }

    /** Notify the view that cart data has updated */
    private val cartDataUpdateMutableLiveData : MutableLiveData<List<ProductCategory>?> by lazy { MutableLiveData<List<ProductCategory>?>() }
    val cartDataUpdateLiveData: LiveData<List<ProductCategory>?> by lazy { cartDataUpdateMutableLiveData }

    /** Notify the view that cart is empty or not empty */
    private val isCartEmptyMutableLiveData : MutableLiveData<Event<Boolean>> by lazy { MutableLiveData<Event<Boolean>>() }
    val isCartEmptyLiveData: LiveData<Event<Boolean>> by lazy { isCartEmptyMutableLiveData }

    /** Notify the view that the total price of cart products has updated */
    private val totalPriceUpdateMutableLiveData : MutableLiveData<Float?> by lazy { MutableLiveData<Float?>() }
    val totalPriceUpdateLiveData: LiveData<Float?> by lazy { totalPriceUpdateMutableLiveData }

    /** Notify the view that current amount of product is valid to add to cart or not */
    private val canAddToCartMutableLiveData : MutableLiveData<Event<Boolean>> by lazy { MutableLiveData<Event<Boolean>>() }
    val canAddToCartLiveData: LiveData<Event<Boolean>> by lazy { canAddToCartMutableLiveData }

    /**
     * Notify to show a short toast message
     * first item: String resource id
     * second item: string parameter
     * */
    private val showToastMutableLiveData : MutableLiveData<Event<Pair<Int, Int?>>> by lazy { MutableLiveData<Event<Pair<Int, Int?>>>() }
    val showToastLiveData: LiveData<Event<Pair<Int, Int?>>> by lazy { showToastMutableLiveData }

    /** map that keep the product id and product instance mapping */
    private val productsIdMap: ConcurrentHashMap<String, Product> = ConcurrentHashMap()

    init {
        loadProductCategories()
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Check if cart is empty or not */
    fun isCartEmpty(): Boolean {
        return isCartEmptyLiveData.value?.peekContent() ?: true
    }

    /** When user clicking on list product item, navigate to product detail page */
    fun onClickListProduct(id: String){
        navigationCommandsSLE.value = Event(
            NavigationCommand.To(
                ProductListFragmentDirections.actionProductListFragmentToProductDetailFragment(id)
            )
        )
    }

    /** When user clicking on cart icon, navigate to product cart page */
    fun onClickListCart(){
        navigationCommandsSLE.value = Event(
            NavigationCommand.To(
                ProductListFragmentDirections.actionProductListFragmentToProductCartFragment()
            )
        )
    }

    /** When user clicking on cart icon, navigate to product cart page */
    fun onClickDetailCart(){
        navigationCommandsSLE.value = Event(
            NavigationCommand.To(
                ProductDetailFragmentDirections.actionProductDetailFragmentToProductCartFragment()
            )
        )
    }

    /** When user clicking on cart product item, navigate to product detail page */
    fun onClickCartProduct(id: String){
        navigationCommandsSLE.value = Event(
            NavigationCommand.To(
                ProductCartFragmentDirections.actionProductCartFragmentToProductDetailFragment(id)
            )
        )
    }

    /** Update product item's amount when the user click on add to cart button in detail page */
    fun onClickAddToCart(id: String?, amount: String?){
        if(id == null || amount == null) return
        val product = productsIdMap[id] ?: return
        val newAmount = product.getAmount() + amount.toInt()
        if(newAmount > AmountActionView.MAX_AMOUNT) {
            showToastMutableLiveData.value = Event(Pair(R.string.product_detail_add_over_message, AmountActionView.MAX_AMOUNT))
            product.setAmount(AmountActionView.MAX_AMOUNT)
        }else{
            showToastMutableLiveData.value = Event(Pair(R.string.product_detail_add_message, amount.toInt()))
            product.setAmount(newAmount)
        }
        updateProductCartData()
    }

    /** Get detail data from productsIdMap and notify observer to update */
    fun getDetailData(id: String){
        val product = productsIdMap[id] ?: return
        detailDataUpdateMutableLiveData.value = product
    }


    /**
     * When cart products are updated, call this function to refresh the amount and item
     * @param saveToCache if true, save the updated data to cache
     * */
    fun updateProductCartData(saveToCache: Boolean = true) = viewModelScope.launch{
        val cartCategories = ArrayList<ProductCategory>()
        var totalPrice = 0.0f

        withContext(Dispatchers.Default) {
            listDataUpdateLiveData.value?.forEach lit@{ category ->
                if (!category.hasCartProduct())
                    return@lit

                // copy new instance of product category, otherwise it will affect the list category data
                val copiedCategory = category.copy(name = category.name, productList = ArrayList())
                // filter out the products that is in the cart
                val cartProducts = category.productList.filter { it.isCartProduct() }
                cartProducts.forEach { // calculate the total price of cart product
                    totalPrice += (it.getAmount() * it.price.toFloat())
                }
                copiedCategory.productList = cartProducts
                cartCategories.add(copiedCategory)
            }
        }

        if(saveToCache)
            saveCartCachedData(cartCategories)

        totalPriceUpdateMutableLiveData.value = totalPrice
        cartDataUpdateMutableLiveData.value = cartCategories
        isCartEmptyMutableLiveData.value = Event(cartCategories.isNullOrEmpty())
    }

    /** Check if updated amount is valid */
    fun updateDetailProductAmount(amount: String?){
        if(amount == null || amount.isBlank() || amount.toInt() <= 0){
            canAddToCartMutableLiveData.value = Event(false)
        } else {
            canAddToCartMutableLiveData.value = Event(true)
        }
    }

    /** When the user modify the amount of the product in the cart, update data and UI */
    fun updateCartProductAmount(id: String?, amount: String?){
        if(id == null || amount == null) return
        val product = productsIdMap[id] ?: return
        product.setAmount(amount.toInt())
        updateProductCartData()
    }

    /** Clear the cart */
    fun clearCartProducts(){
        val categories = cartDataUpdateLiveData.value
        if(categories.isNullOrEmpty()) return
        categories.forEach { category ->
            category.productList.forEach {
                it.setAmount(0)
            }
        }
        updateProductCartData(true)
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
        // after the category data is initialized, get the cart product information form cache
        loadCartCachedData()
        listDataUpdateMutableLiveData.value = categories
    }

    /** Get the cart products information from cache */
    private fun loadCartCachedData() = CoroutineScope(Dispatchers.IO).launch{
        ProductDataSource().getCachedCartProducts(
            callback = object : CacheCallback<List<ProductCategory>?> {
                override fun onResult(data: List<ProductCategory>?) {
                    Log.d(TAG, "loadCartCachedData success, data:$data")
                    if(data == null) return

                    data.forEach {category ->
                        category.productList.forEach lit@{ it ->
                            val product = productsIdMap[it.id] ?: return@lit
                            product.setAmount(it.getAmount())
                        }
                    }
                    updateProductCartData(false)
                }

                override fun onError(message: String) {
                    Log.w(TAG, "loadCartCachedData fail, message:$message")
                }

            },
        )
    }

    /** Save the cart products information to cache */
    private fun saveCartCachedData(data: List<ProductCategory>?) = CoroutineScope(Dispatchers.IO).launch{
        ProductDataSource().saveCachedCartProducts(
            callback = object : CacheCallback<List<ProductCategory>?>{
                override fun onResult(data: List<ProductCategory>?) {
                    Log.d(TAG, "saveCartCachedData success, data:$data")
                }

                override fun onError(message: String) {
                    Log.w(TAG, "saveCartCachedData fail, message:$message")
                }
            },
            data = data
        )
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================

}