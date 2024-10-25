package com.example.dasada.recyclerview.layoutmanager

import android.content.Context
import android.view.View
import android.view.View.LAYOUT_DIRECTION_RTL
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * Class that helps with snapping view to "whole pages". Works only when RecyclerView is setup with [CustomLayoutManager].
 */
class CustomSnapHelper : LinearSnapHelper() {

    private var context: Context? = null
    private var orientationHelper: OrientationHelper? = null
    private var scroller: Scroller? = null
    private var isRTL: Boolean = false // could inject RTL and LTR classes based on orientation instead of doing "if"s

    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        if (recyclerView != null) {
            context = recyclerView.context
            scroller = Scroller(context, DecelerateInterpolator())
            isRTL = recyclerView.layoutDirection == LAYOUT_DIRECTION_RTL
        } else {
            scroller = null
            context = null
            isRTL = false
        }
        super.attachToRecyclerView(recyclerView)
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
        val helper = getOrCreateOrientationHelper(layoutManager)
        val customLayoutManager = (layoutManager as? CustomLayoutManager) ?: return null
        val childCount = customLayoutManager.childCount
        if (childCount == 0) return null
        val itemsPerPage = customLayoutManager.itemsPerPage
        val firstItemOfAPageIndex = (0..customLayoutManager.itemCount step itemsPerPage).toList()

        var absClosest = Integer.MAX_VALUE
        var closestView: View? = null
        for (i in 0 until childCount) {
            val child = customLayoutManager.getChildAt(i) as View
            val position = customLayoutManager.getPosition(child)
            if (position !in firstItemOfAPageIndex) continue

            val delta = abs(getDistanceToScreenEdge(child, helper, isRTL))
            if (delta < absClosest) {
                absClosest = delta
                closestView = child
            }
        }
        return closestView
    }

    private fun getDistanceToScreenEdge(targetView: View, orientationHelper: OrientationHelper, isRTL: Boolean) =
        if (isRTL) getCoordinateDeltaRLT(targetView, orientationHelper)
        else getCoordinateDeltaLTR(targetView, orientationHelper)

    private fun getCoordinateDeltaLTR(targetView: View, orientationHelper: OrientationHelper): Int {
        val childCoordinate = orientationHelper.getDecoratedStart(targetView)
        val baseCoordinate = orientationHelper.startAfterPadding
        return childCoordinate - baseCoordinate
    }

    private fun getCoordinateDeltaRLT(targetView: View, orientationHelper: OrientationHelper): Int {
        val childCoordinate = orientationHelper.getDecoratedStart(targetView) + orientationHelper.getDecoratedMeasurement(targetView)
        val baseCoordinate = orientationHelper.endAfterPadding
        return childCoordinate - baseCoordinate
    }

    override fun calculateDistanceToFinalSnap(layoutManager: RecyclerView.LayoutManager, targetView: View): IntArray {
        val out = IntArray(2)
        out[0] = distanceToEdge(targetView, getOrCreateOrientationHelper(layoutManager))
        return out
    }

    private fun distanceToEdge(targetView: View, helper: OrientationHelper): Int =
        if (isRTL)
            helper.getDecoratedEnd(targetView) - helper.endAfterPadding
        else
            helper.getDecoratedStart(targetView) - helper.startAfterPadding


    private fun getOrCreateOrientationHelper(layoutManager: RecyclerView.LayoutManager?): OrientationHelper =
        orientationHelper ?: OrientationHelper.createHorizontalHelper(layoutManager).also { orientationHelper = it }
}
