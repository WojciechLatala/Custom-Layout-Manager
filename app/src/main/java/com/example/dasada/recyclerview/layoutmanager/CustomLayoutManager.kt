package com.example.dasada.recyclerview.layoutmanager

import android.content.Context
import android.view.View
import android.view.View.LAYOUT_DIRECTION_RTL
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.example.dasada.common.ceilDiv

class CustomLayoutManager(private val context: Context, columns: Int, rows: Int) : RecyclerView.LayoutManager() {

    private val safeColumnsCount = if (columns <= 0) MIN_SIZE else columns
    private val safeRowsCount = if (rows <= 0) MIN_SIZE else rows
    private val directionalOffsetHelper: DirectionalOffsetHelper by lazy { if (layoutDirection == LAYOUT_DIRECTION_RTL) RTLOffsetHelper() else LTROffsetHelper() }

    private var scrollOffset = 0

    val itemsPerPage = safeColumnsCount * safeRowsCount

    override fun isAutoMeasureEnabled(): Boolean = true

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams = RecyclerView.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        recycler?.let { fill(recycler = it) }
    }

    override fun canScrollHorizontally(): Boolean = true

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        if (childCount == 0 || dx == 0 || recycler == null) {
            recycler?.let { detachAndScrapAttachedViews(it) }
            return 0
        }
        val lastScrollOffset = scrollOffset
        val additionalPages = itemCount.ceilDiv(itemsPerPage) - 1
        scrollOffset = directionalOffsetHelper.getMaxScrollOffset(dx, additionalPages, scrollOffset, width)
        fill(recycler = recycler)
        return scrollOffset - lastScrollOffset
    }

    fun smoothScrollToPage(pageIndex: Int) {
        if (itemCount == 0) return
        val lastPageIndex = itemCount.ceilDiv(itemsPerPage) - 1
        val targetPosition = when {
            pageIndex < 0 -> 0
            pageIndex > lastPageIndex -> lastPageIndex * itemsPerPage
            else -> pageIndex * itemsPerPage
        }
        val smoothScroller: SmoothScroller = CustomSmoothScroller(context, directionalOffsetHelper)
        smoothScroller.targetPosition = targetPosition
        startSmoothScroll(smoothScroller)
    }

    /**
     * Moves the page view "back" from empty page when all items on it were removed.
     * This solution is, however, janky. It would probably be better to move invocation from diffutil callback to recyclerview animation end.
     */
    fun scrollIfNoItemsAreVisible() {
        val pageCount = itemCount.ceilDiv(itemsPerPage)
        if (directionalOffsetHelper.isOutOfScrollBounds(scrollOffset, pageCount, width))
            smoothScrollToPage(pageCount - 1)
    }

    private fun fill(recycler: RecyclerView.Recycler) {
        detachAndScrapAttachedViews(recycler)
        val itemSize = width / safeColumnsCount // recycler height will auto scale
        for (index in 0 until itemCount) {
            val view = recycler.getViewForPosition(index)
            addView(view)
            setLayoutParamsForItem(view, itemSize)
            val (left, right) = directionalOffsetHelper.getChildHorizontalBounds(index, itemSize, itemsPerPage, safeColumnsCount, scrollOffset, width)
            val (top, bottom) = directionalOffsetHelper.getChildVerticalBounds(index, itemSize, itemsPerPage, safeColumnsCount)
            measureChildWithMargins(view, itemSize, itemSize)
            layoutDecoratedWithMargins(view, left, top, right, bottom)
        }
        recycler.scrapList.toList().forEach {// copy list to avoid ConcurrentModificationException
            recycler.recycleView(it.itemView)
        }
    }

    private fun setLayoutParamsForItem(view: View, itemSize: Int) {
        val layoutParams = view.layoutParams as RecyclerView.LayoutParams
        layoutParams.width = itemSize
        layoutParams.height = itemSize
        view.layoutParams = layoutParams
    }

    private companion object {
        const val MIN_SIZE = 1
    }
}
