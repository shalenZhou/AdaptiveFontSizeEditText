package com.example.adaptivefontsizeedittext

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText

private const val TAG = "AutoAdjustSizeEditText"

class AutoAdjustSizeEditText : AppCompatEditText {
    private var minTextSize: Float = 0f
    private var maxTextSize: Float = 0f

    /**
     * 判断输入文本字体是否变小过
     */
    private var hasScaleSmall = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        // 读取自定义属性，获取设置字体的大小范围
        val array = context.obtainStyledAttributes(attrs, R.styleable.AutoAdjustSizeEditText)
        minTextSize = array.getDimension(
            R.styleable.AutoAdjustSizeEditText_minTextSize,
            context.resources.getDimension(R.dimen.edit_text_size_min)
        )
        // 使用当前字体大小作为最大值
        maxTextSize = textSize
        // 回收 TypedArray
        array.recycle()

        // 如果设置的最大值 & 最小值不正确（例如：minTextSize > maxTextSize），则互换
        if (minTextSize > maxTextSize) {
            val temp = minTextSize
            minTextSize = maxTextSize
            maxTextSize = temp
        }

        Log.d(TAG, "minTextSize = $minTextSize, maxTextSize = $maxTextSize")
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        adjustTextSize(this)
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w != oldw) {
            adjustTextSize(this)
        }
        super.onSizeChanged(w, h, oldw, oldh)
    }

    private fun adjustTextSize(textView: TextView) {
        val text = textView.text.toString()
        val textWidth = textView.width

        if (text.isEmpty() || textWidth <= 0) {
            return
        }

        // 获取输入框可输入的文本长度
        val maxInputWidth = textView.width - textView.compoundPaddingStart - textView.compoundPaddingEnd - maxTextSize
        // 获取当前文本字体大小
        var currentTextSize = textView.textSize

        Log.d(TAG, "currentTextSize = $currentTextSize")

        // 设置画笔的字体大小
        paint.textSize = currentTextSize

        /**
         * 循环减小字体大小
         * 当    1. 文本字体大小大于最小值 2. 可输入文本长度小于已输入文本长度    时
         */
        while ((currentTextSize > minTextSize) && (maxInputWidth < paint.measureText(text))) {
            hasScaleSmall = true

            Log.d(TAG, "TextSizeChangeSmall = $currentTextSize")

            --currentTextSize

            if (currentTextSize < minTextSize) {
                currentTextSize = minTextSize
                break
            }

            // 设置画笔的字体大小
            paint.textSize = currentTextSize
        }

        /**
         * 循环增大字体大小
         * 当    1. 文本字体大小小于最大值 2. 可输入文本长度大于已输入文本长度    时
         */
        while (hasScaleSmall && (currentTextSize < maxTextSize) && (maxInputWidth > paint.measureText(
                text
            ))
        ) {
            Log.d(TAG, "TextSizeChangeBig = $currentTextSize")

            ++currentTextSize

            if (currentTextSize > maxTextSize) {
                currentTextSize = maxTextSize
                break
            }

            // 设置画笔的字体大小
            paint.textSize = currentTextSize
        }

        // 设置文本字体
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize)

        Log.d(TAG, "currentTextSizeAfter = $currentTextSize")
    }
}