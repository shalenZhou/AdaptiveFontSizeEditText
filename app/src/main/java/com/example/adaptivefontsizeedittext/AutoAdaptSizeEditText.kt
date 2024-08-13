package com.example.adaptivefontsizeedittext

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText

class AutoAdaptSizeEditText : AppCompatEditText {
    private var minTextSize: Float = 0f
    private var maxTextSize: Float = 0f

    /**
     * 判断输入文本字体是否变小过
     */
    private var hasScaleSmall = false

    /**
     * 可输出文本的最大长度
     */
    private var length = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        // 读取自定义属性，获取设置字体的大小范围
        val array = context.obtainStyledAttributes(attrs, R.styleable.AutoAdaptSizeEditText)
        minTextSize = array.getDimension(
            R.styleable.AutoAdaptSizeEditText_minSize,
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
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        autoAdaptTextSize(this)
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w != oldw) {
            autoAdaptTextSize(this)
        }
        super.onSizeChanged(w, h, oldw, oldh)
    }

    private fun autoAdaptTextSize(textView: TextView) {
        val text = textView.text.toString()
        val textWidth = textView.width

        if (text.isEmpty() || textWidth <= 0) {
            return
        }

        // 获取输入框可输入的文本长度
        val maxInputWidth =
            textView.width - textView.paddingStart - textView.paddingEnd - maxTextSize
        // 获取当前文本字体大小
        var currentTextSize = textView.textSize

        // 设置画笔的字体大小
        paint.textSize = currentTextSize

        /**
         * 循环减小字体大小
         * 当    1. 文本字体大小大于最小值 2. 可输入文本长度小于已输入文本长度    时
         */
        while ((currentTextSize > minTextSize) && (maxInputWidth < paint.measureText(text))) {
            hasScaleSmall = true

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
            ++currentTextSize

            if (currentTextSize > maxTextSize) {
                currentTextSize = maxTextSize
                break
            }

            // 设置画笔的字体大小
            paint.textSize = currentTextSize
        }

        /**
         * 限制输入条件：
         * 1. 当前字体大小已经是最小字体大小
         * 2. 所有字体大小的宽度对比控件的最大宽度
         */
        if (currentTextSize == minTextSize && paint.measureText(text) > maxInputWidth) {
            if (text.length > length) {
                setText(text.substring(0, length))
                // 光标设置尾部
                setSelection(length)
            }

            // 最大可输入字符数，限制输入的关键点
            setEms(length)
        } else {
            length = text.length
        }

        // 设置文本字体
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize)
    }
}