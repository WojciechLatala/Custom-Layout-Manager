package com.example.dasada.recyclerview.layoutmanager

import android.content.Context
import androidx.recyclerview.widget.LinearSmoothScroller

class CustomSmoothScroller(context: Context, private val directionalOffsetHelper: DirectionalOffsetHelper) : LinearSmoothScroller(context) {
    override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int =
        directionalOffsetHelper.getScrollTargetDeltaForView(viewStart, viewEnd, boxStart, boxEnd, snapPreference)
}
