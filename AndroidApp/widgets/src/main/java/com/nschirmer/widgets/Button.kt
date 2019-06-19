package com.nschirmer.widgets

import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.*
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.core.graphics.ColorUtils
import kotlinx.android.synthetic.main.button.view.*


/**
 * Custom Button View class that uses the [OutlineShadowView] as shadow.
 *
 * @sample
 * Usage in XML:
        <com.nschirmer.widgets.Button
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"

            app:title="Something"
            app:description="Hello sir"
            app:color="@color/blue"
            app:text_color="@color/white" />
 * **/

class Button @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0): FrameLayout(context, attrs, defStyle) {

    companion object {
        private const val BRIGHTNESS_REDUCTION = 0.8f
        private const val PRESS_DURATION = 200 //ms
        private const val SHADOW_ALPHA = 120
    }

    /**
     * Change the label of the button.
     * **/
    var title : String
        set(text){
            button.text = text

        } get() = button.text.toString()


    /**
     * Accessibility content description. The default value is the [title].
     * **/
    var description: String?
        set(description){
            button.contentDescription = description

        } get() = button.contentDescription.toString()


    /**
     * Change the button and shadow color.
     * @param color is a [Color]
     * **/
    var color: Int?
        set(color) {
            if(color != null) {
                setButtonSelector(color)
                shadowColor = color
            }
        } get() = button.background.state[1]


    /**
     * Change the label text color.
     * @param color is a [Color]
     * **/
    var textColor: Int?
        set(color){
            if(color != null) button.setTextColor(color)

        } get() = button.currentTextColor


    private var shadowColor: Int?
        set(color) {
            if(color != null) button_shadow.color = ColorUtils.setAlphaComponent(color, SHADOW_ALPHA)

        } get() = button_shadow.color



    init {
        LayoutInflater.from(context).inflate(R.layout.button, this, true)
        setAttributes(context.obtainStyledAttributes(attrs, R.styleable.Button))
    }


    private fun setAttributes(typedArray: TypedArray){
        try {
            title = typedArray.getString(R.styleable.Button_title) ?: title
            description = typedArray.getString(R.styleable.Button_description) ?: title
            color = typedArray.getColor(R.styleable.Button_color, Color.GRAY)
            textColor = typedArray.getColor(R.styleable.Button_text_color, Color.BLACK)


        } finally {
            typedArray.recycle()
        }
    }



    fun setOnClickListener(listener: (View) -> Unit){
        button.setOnClickListener {
            listener(it)
        }
    }


    fun setOnLongClickListener(listener: (View) -> Unit){
        button.setOnLongClickListener {
            listener(it)
            true
        }
    }



    private fun setButtonSelector(color: Int){
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                button.background = ColorDrawable(color)
                button.background = setColorRipple(color)
                button.stateListAnimator = null
            }
            else -> button.background = setColorSelection(color)
        }
    }


    private fun setColorSelection(color: Int): StateListDrawable {
        StateListDrawable().run {
            setExitFadeDuration(PRESS_DURATION)
            addState(intArrayOf(android.R.attr.state_pressed), getRoundedTintedDrawable(getDarkerColor(color)))
            addState(intArrayOf(), getRoundedTintedDrawable(color))
            return this
        }
    }


    private fun getRoundedTintedDrawable(color: Int): Drawable {
        PaintDrawable().run {
            val radius = resources.getDimension(R.dimen.button_corner_radius)
            shape = RoundRectShape(floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius), null, null)
            setColorFilter(color, PorterDuff.Mode.SRC_IN)
            return this
        }
    }


    /**
     * @return a [Color] darker than [color]
     * **/
    private fun getDarkerColor(color: Int): Int{
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] *= BRIGHTNESS_REDUCTION // change the brightness of the color
        return Color.HSVToColor(hsv)
    }


    @RequiresApi(21) @TargetApi(21)
    private fun setColorRipple(color: Int): RippleDrawable {
        button.background = getRoundedTintedDrawable(color)
        return RippleDrawable(ColorStateList(
            arrayOf(intArrayOf()),
            intArrayOf(getDarkerColor(color))),
            button.background, null)
    }


}