package net.edgwbs.bookstorage.utils

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import info.androidhive.fontawesome.FontCache
import info.androidhive.fontawesome.R

class FontAwesomeTextView: AppCompatTextView {
    private var isBrandingIcon: Boolean = true
    private var isSolidIcon: Boolean = true

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs, 0) {
        init(context, attrs)
    }

    constructor(context: Context,attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
        init(context, attrs)
    }

    private fun init(context: Context,attrs: AttributeSet) {
        val a: TypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.FontTextView,
            0, 0)
        isSolidIcon = a.getBoolean(R.styleable.FontTextView_solid_icon, true)
        isBrandingIcon = a.getBoolean(R.styleable.FontTextView_brand_icon, false)


        if (isBrandingIcon)
            typeface = FontCache.get(getContext(), "fa-brands-400.ttf")
        else if (isSolidIcon)
            typeface = FontCache.get(getContext(), "fa-solid-900.ttf")
        else
            typeface = FontCache.get(getContext(), "fa-regular-400.ttf")
    }
}
class FontAwesomeButtonView: AppCompatButton {
    private var isBrandingIcon: Boolean = true
    private var isSolidIcon: Boolean = true

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs, 0) {
        init(context, attrs)
    }

    constructor(context: Context,attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
        init(context, attrs)
    }

    private fun init(context: Context,attrs: AttributeSet) {
        val a: TypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.FontTextView,
            0, 0)
        isSolidIcon = a.getBoolean(R.styleable.FontTextView_solid_icon, true)
        isBrandingIcon = a.getBoolean(R.styleable.FontTextView_brand_icon, false)


        if (isBrandingIcon)
            typeface = FontCache.get(getContext(), "fa-brands-400.ttf")
        else if (isSolidIcon)
            typeface = FontCache.get(getContext(), "fa-solid-900.ttf")
        else
            typeface = FontCache.get(getContext(), "fa-regular-400.ttf")
    }
}
