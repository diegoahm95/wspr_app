package com.app.wesperassignment.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class BoardCellView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): FrameLayout(context, attrs, defStyleAttr) {

    private var listener: OnCellTapListener? = null
    private var position: Int = -1

    init {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
    }

    fun setupCell(pos: Int, l: OnCellTapListener) {
        position = pos
        listener = l
        setOnClickListener { listener?.onCellTapped(position) }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

}