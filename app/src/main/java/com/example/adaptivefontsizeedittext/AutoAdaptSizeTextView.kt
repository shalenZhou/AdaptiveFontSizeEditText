package com.example.adaptivefontsizeedittext

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.ceil

class AutoAdaptSizeTextView : AppCompatTextView {
    private var minTextSize: Float = 0f
    private var origTextSize: Float = 0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        val array =
            context.obtainStyledAttributes(attrs, R.styleable.AutoAdaptSizeTextView)
        minTextSize = array.getDimension(
            R.styleable.AutoAdaptSizeTextView_minTextViewSize,
            context.resources.getDimension(R.dimen.edit_text_size_min)
        )
        origTextSize = textSize
        array.recycle()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        changeTextSize()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        paint.textSize = origTextSize
        val fontMetrics = paint.fontMetrics
        val singleLineHeight = ceil(fontMetrics.bottom - fontMetrics.top).toInt()
        setMeasuredDimension(measuredWidth, paddingTop + singleLineHeight + paddingBottom)
    }

    private fun changeTextSize() {
        if (text.isNullOrEmpty()) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, origTextSize)
        } else {
            val textWidth = paint.measureText(text.toString())
            val totalWidth = width - compoundPaddingStart - compoundPaddingEnd - origTextSize
            if (textWidth > totalWidth && textSize > minTextSize) {
                while (paint.measureText(text.toString()) > totalWidth && textSize > minTextSize) {
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 1f)
                }
            } else if (textWidth < totalWidth && textSize < origTextSize) {
                while (paint.measureText(text.toString()) < totalWidth && textSize < origTextSize) {
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize + 1f)
                }
                if (paint.measureText(text.toString()) > totalWidth) {
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 1f)
                }
            }
        }
    }
}