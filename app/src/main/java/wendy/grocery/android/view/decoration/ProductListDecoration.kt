package wendy.grocery.android.view.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import wendy.grocery.android.R
import wendy.grocery.android.application.WendyGroceryApplication

class ProductListDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        parent.adapter?.let {
            val itemPosition = parent.getChildAdapterPosition(view)
            if (itemPosition == RecyclerView.NO_POSITION) {
                return
            }
            if (itemPosition == it.itemCount - 1) {
                outRect.bottom =
                    WendyGroceryApplication.getInstance().resources.getDimensionPixelSize(R.dimen.spacing_small)

            }
            else outRect.bottom = 0
        }
    }
}