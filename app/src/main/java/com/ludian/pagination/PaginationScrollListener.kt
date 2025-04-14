package com.ludian.pagination

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PaginationScrollListener(
    private val layoutManager: LinearLayoutManager,
    private val loadMore: () -> Unit
) : RecyclerView.OnScrollListener() {

    private var isLoading = false
    private var isLastPage = false

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        if (!isLoading && !isLastPage) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount - 5) {
                loadMore()
                isLoading = true
            }
        }
    }

    fun setLoaded() {
        isLoading = false
    }

    fun setLastPage(isLastPage: Boolean) {
        this.isLastPage = isLastPage
    }
}




/*class PaginationScrollListener(
    private val layoutManager: LinearLayoutManager,
    private val loadMore: () -> Unit,
    private val pageSize: Int
) : RecyclerView.OnScrollListener() {

    private var isLoading = false
    private var isLastPage = false
    private var totalItemCount = 0
    private var currentPage = 1

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        *//*   val visibleItemCount = layoutManager.childCount
           val totalItemCount = layoutManager.itemCount
           val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

           if (!isLoading && !isLastPage) {
               if (visibleItemCount + firstVisibleItemPosition >= totalItemCount - 5) {
                   loadMore()
                   isLoading = true
               }
           }*//*


        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        // Trigger loadMore() if we're not loading and not on the last page
        if (!isLoading && !isLastPage) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount - 5) {
                loadMore()
                isLoading = true
                currentPage = totalItemCount / pageSize

                Log.e("total item ======", totalItemCount.toString());
            // Calculate the current page number
            }
        }


    }

    fun setLoaded() {
        isLoading = false
    }

    fun setLastPage(isLastPage: Boolean) {
        this.isLastPage = isLastPage
    }

    fun getCurrentPage(): Int {
        return currentPage
    }
}*/



