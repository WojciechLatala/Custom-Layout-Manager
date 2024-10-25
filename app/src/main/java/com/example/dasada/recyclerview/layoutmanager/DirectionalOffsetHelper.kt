package com.example.dasada.recyclerview.layoutmanager

import kotlin.math.max
import kotlin.math.min

abstract class DirectionalOffsetHelper {
    abstract fun getMaxScrollOffset(dx: Int, additionalPages: Int, scrollOffset: Int, width: Int): Int

    abstract fun getScrollTargetDeltaForView(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int

    abstract fun isOutOfScrollBounds(scrollOffset: Int, pageCount: Int, width: Int): Boolean

    abstract fun getChildHorizontalBounds(index: Int, size: Int, itemsPerPage: Int, safeColumnsCount: Int, scrollOffset: Int, width: Int): Pair<Int, Int>

    fun getChildVerticalBounds(index: Int, size: Int, itemsPerPage: Int, safeColumnsCount: Int): Pair<Int, Int> {
        val positionInAPage = index % itemsPerPage
        val itemRow = positionInAPage.floorDiv(safeColumnsCount)
        val top = itemRow * size
        return top to top + size
    }
}

class RTLOffsetHelper : DirectionalOffsetHelper() {
    override fun getMaxScrollOffset(dx: Int, additionalPages: Int, scrollOffset: Int, width: Int): Int =
        min(max(scrollOffset + dx, additionalPages * -width), 0)

    override fun getScrollTargetDeltaForView(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int = boxEnd - viewEnd
    override fun isOutOfScrollBounds(scrollOffset: Int, pageCount: Int, width: Int) = scrollOffset < -((pageCount - 1) * width)

    override fun getChildHorizontalBounds(index: Int, size: Int, itemsPerPage: Int, safeColumnsCount: Int, scrollOffset: Int, width: Int): Pair<Int, Int> {
        val currentPage = index.floorDiv(itemsPerPage)
        val positionInPage = index % itemsPerPage % safeColumnsCount
        val distanceFromStart = (currentPage * safeColumnsCount + positionInPage) * size
        val viewStart = width - distanceFromStart - scrollOffset
        return viewStart - size to viewStart
    }
}

class LTROffsetHelper : DirectionalOffsetHelper() {
    override fun getMaxScrollOffset(dx: Int, additionalPages: Int, scrollOffset: Int, width: Int): Int =
        min(max(scrollOffset + dx, 0), additionalPages * width)

    override fun getScrollTargetDeltaForView(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int = boxStart - viewStart

    override fun isOutOfScrollBounds(scrollOffset: Int, pageCount: Int, width: Int) = scrollOffset > ((pageCount - 1) * width)

    override fun getChildHorizontalBounds(index: Int, size: Int, itemsPerPage: Int, safeColumnsCount: Int, scrollOffset: Int, width: Int): Pair<Int, Int> {
        val currentPage = index.floorDiv(itemsPerPage)
        val positionInPage = index % itemsPerPage % safeColumnsCount
        val distanceFromStart = (currentPage * safeColumnsCount + positionInPage) * size
        val viewStart = distanceFromStart - scrollOffset
        return viewStart to viewStart + size
    }
}
