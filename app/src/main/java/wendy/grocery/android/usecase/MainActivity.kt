package wendy.grocery.android.usecase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import androidx.activity.viewModels
import wendy.grocery.android.R

class MainActivity : AppCompatActivity() {

    // ===========================================================
    // Fields
    // ===========================================================

    private val viewModel : ProductViewModel by viewModels()

    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.onClickBack()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

}