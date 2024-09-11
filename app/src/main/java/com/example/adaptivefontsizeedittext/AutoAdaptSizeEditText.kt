package com.example.adaptivefontsizeedittext

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatEditText

class AutoAdaptSizeEditText : AppCompatEditText {
    private var origTextSize: Float = 0f

    private val watcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (isEnabled) {
                changeTextSize()
            }
        }
    }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        origTextSize = textSize
        addTextChangedListener(watcher)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeTextChangedListener(watcher)
    }

    private fun changeTextSize() {
        if (text.isNullOrEmpty()) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, origTextSize)
        } else {
            val textWidth = paint.measureText(text.toString())
            val totalWidth = width - compoundPaddingStart - compoundPaddingEnd
            if (textWidth > totalWidth) {
                while (paint.measureText(text.toString()) > totalWidth) {
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