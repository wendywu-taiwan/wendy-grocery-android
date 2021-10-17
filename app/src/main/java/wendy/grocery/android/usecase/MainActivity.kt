package wendy.grocery.android.usecase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import wendy.grocery.android.R

class MainActivity : AppCompatActivity() {

    // ===========================================================
    // Fields
    // ===========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true
    }

}