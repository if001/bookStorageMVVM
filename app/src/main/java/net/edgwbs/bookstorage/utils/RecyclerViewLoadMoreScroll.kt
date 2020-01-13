package net.edgwbs.bookstorage.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewLoadMoreScroll(layoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {

    private lateinit var mOnLoadMoreListener: OnLoadMoreListener
    private var isLoading: Boolean = false
    private var lastVisibleItem: Int = 0
    private var totalItemCount:Int = 0
    private var mLayoutManager: RecyclerView.LayoutManager = layoutManager

    fun setLoaded() {
        isLoading = false
    }

    fun setLoading() {
        isLoading = true
    }

    fun getLoaded(): Boolean {
        return isLoading
    }

    fun setOnLoadMoreListener(mOnLoadMoreListener: OnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy <= 0) return

        totalItemCount = mLayoutManager.itemCount

        if (mLayoutManager is LinearLayoutManager) {
            lastVisibleItem = (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        }

        // if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
        if (!isLoading && totalItemCount == lastVisibleItem + 1) {
            mOnLoadMoreListener.onLoadMore()
        }
    }
}

interface OnLoadMoreListener {
    fun onLoadMore()
}