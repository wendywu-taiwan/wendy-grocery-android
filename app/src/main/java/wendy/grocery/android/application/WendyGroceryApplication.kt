package wendy.grocery.android.application

import androidx.multidex.MultiDexApplication

class WendyGroceryApplication : MultiDexApplication(){

    companion object {
        private lateinit var mApp : WendyGroceryApplication
        fun getInstance() : WendyGroceryApplication {
            return mApp
        }

    }
}