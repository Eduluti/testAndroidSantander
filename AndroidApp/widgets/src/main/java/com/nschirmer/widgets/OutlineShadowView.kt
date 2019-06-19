package com.nschirmer.widgets

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout


/**
 *  This class create a shadow for a Child View, by creating a blurred bitmap within the Child View perimeter.
 *
 *  You can use it for a single View or for a hole group of Views inside a Layout Group (LinearLayout, ConstraintLayout...)
 *  note: all views inside the Layout Group will have the shadows.
 *
 *  @sample
 *  Usage on XML:
        <com.nschirmer.widgets.OutlineShadowView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"

            app:shadow_distance="2dp"
            app:shadow_radius="10dp"
            app:has_shadow="true"
            app:shadow_color="@color/blue">

            <Some View or Layout Group
                android:layout_width="match_parent"
                android:layout_height="wrap_content"/>

        </com.nschirmer.widgets.OutlineShadowView>
 *
 */

//This class has a lot of inspiration from ShadowLayout by Devlight
class OutlineShadowView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0): FrameLayout(context, attrs, defStyle) {

    companion object {
        // Default internal shadow values
        const val DEFAULT_RADIUS = 15.0f
        const val DEFAULT_DISTANCE = 10.0f
        const val DEFAULT_ANGLE = 90.0f
        const val DEFAULT_COLOR = Color.DKGRAY

        // Internal shadow bounds values
        const val MAX_ALPHA = 255
        const val MIN_RADIUS = 0.1f
        const val MAX_ANGLE = 360.0f
        const val MIN_ANGLE = 0.0f
    }


    // Shadow image
    private val paint = object : Paint(ANTI_ALIAS_FLAG) {
        init {
            isDither = true
            isFilterBitmap = true
        }
    }
    private var bitmap: Bitmap? = null
    private val bounds = Rect() // not a lazy call because it will be called almost immediately by onMeasure
    private val canvas = Canvas() // not a lazy call because it will be called almost immediately by onMeasure
    private var needToDrawShadow: Boolean = true


    // Internal shadow values
    private var alpha = 0
    private var offsetX = 0f
    private var offsetY = 0f

    // Dynamic shadow values
    /**
     * Color of the shadow. The default value is [DEFAULT_COLOR].
     * **/
    var color = DEFAULT_COLOR
        set(color) {
            field = color
            alpha = Color.alpha(color)
            resetShadow()
        }

    /**
     * Angle of the shadow. You can only set between [MIN_ANGLE] and [MAX_ANGLE].
     * **/
    var angle = DEFAULT_ANGLE
        set(value) {
            if(value in MIN_ANGLE .. MAX_ANGLE){
                field = Math.max(MIN_ANGLE, Math.min(value, MAX_ANGLE))
                resetShadow()
            }
        }

    /**
     * Harsh size of the shadow. The default value is [DEFAULT_DISTANCE].
     * This will emulate the [elevation](https://material.io/design/environment/elevation.html) size.
     * **/
    var distance = DEFAULT_DISTANCE
        set(value) {
            field = value
            resetShadow()
        }

    /**
     * Spread size of the shadow. The default value is [DEFAULT_RADIUS].
     * This will change the lightness of the shadow on the borders. To create a more smooth or harsh shadow.
     * **/
    var radius = DEFAULT_RADIUS
        set(value) {
            field = Math.max(MIN_RADIUS, value)

            if (!isInEditMode) {
                paint.maskFilter = BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
                resetShadow()
            }
        }

    /**
     * Change if the View or Layout Group will have the shadow enabled. It comes enabled by default.
     * **/
    var hasShadow: Boolean = true
        set(hasShadow) {
            field = hasShadow
            postInvalidate()
        }


    // Draw the view attributes on initialization
    init {
        setWillNotDraw(false)
        setLayerType(View.LAYER_TYPE_HARDWARE, paint)
        setAttributes(context.obtainStyledAttributes(attrs, R.styleable.OutlineShadowView))
    }


    private fun setAttributes(typedArray: TypedArray){
        try {
            hasShadow = typedArray.getBoolean(R.styleable.OutlineShadowView_has_shadow, true)
            radius = typedArray.getDimension(R.styleable.OutlineShadowView_shadow_radius, DEFAULT_RADIUS)
            distance = typedArray.getDimension(R.styleable.OutlineShadowView_shadow_distance, DEFAULT_DISTANCE)
            angle = typedArray.getFloat(R.styleable.OutlineShadowView_shadow_angle, DEFAULT_ANGLE)
            color = typedArray.getColor(R.styleable.OutlineShadowView_shadow_color, DEFAULT_COLOR)


            distance

        } finally {
            typedArray.recycle()
        }
    }


    private fun resetShadow(){
        offsetX = (distance * Math.cos(angle / 180.0f * Math.PI)).toFloat()
        offsetY = (distance * Math.sin(angle / 180.0f * Math.PI)).toFloat()

        (distance + radius).toInt().let {
            setPadding(it, it, it, it)
        }

        requestLayout()
    }


    // When the view is not visible anymore, it will clear the shadow to free up some memory
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        clearShadowBitmap()
    }


    private fun clearShadowBitmap(){
        bitmap?.recycle()
        bitmap = null
    }


    // Calculate the raw View bounds to draw the shadow
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setShadowBounds()
    }


    private fun setShadowBounds(){
        bounds.set(0, 0, measuredWidth, measuredHeight)
    }


    // Setting the need to draw the shadow when the View changes or is initializing
    override fun requestLayout() {
        needToDrawShadow = true
        super.requestLayout()
    }


    // If anything occurs with the View visibility, trigger the action to redraw the shadow
    override fun onWindowVisibilityChanged(visibility: Int) {
        needToDrawShadow = true
        super.onWindowVisibilityChanged(visibility)
    }


    // Draw the shadow when the child Views are created
    override fun dispatchDraw(canvasView: Canvas?) {
        if(hasShadow){
            drawShadow(canvasView)
        }
        super.dispatchDraw(canvasView)
    }


    private fun drawShadow(canvasView: Canvas?){
        if(needToDrawShadow){
            redrawShadow()
            drawShadowIntoChild(canvasView)
        }
    }


    private fun drawShadowIntoChild(canvasView: Canvas?) {
        // Set full alpha into the Child View
        paint.color = adjustShadowAlpha(true)
        // Draw shadow
        if(canDrawShadow()){
            canvasView?.drawBitmap(bitmap!!, 0f, 0f, paint)
        }
    }


    private fun adjustShadowAlpha(adjust: Boolean): Int = Color.argb(
        if(adjust) MAX_ALPHA else alpha, Color.red(color), Color.green(color), Color.blue(color)
    )


    private fun canDrawShadow(): Boolean = bitmap != null && !bitmap!!.isRecycled


    private fun redrawShadow(){
        when {
            hasNoBounds() -> redrawBitmapShadow()
            else -> createShadowPlaceHolder()
        }
    }


    private fun hasNoBounds(): Boolean = bounds.width() != 0 && bounds.height() != 0


    private fun redrawBitmapShadow(){
        resetBitmapBounds()
        super.dispatchDraw(canvas)
        configureShadowAlpha()
    }


    private fun configureShadowAlpha(){
        bitmap?.extractAlpha().let {
            if(it != null){
                canvas.drawColor(0, PorterDuff.Mode.CLEAR)
                paint.color = adjustShadowAlpha(false)
                canvas.drawBitmap(it, offsetX, offsetY, paint)
                it.recycle()
            }
        }
    }


    private fun resetBitmapBounds(){
        bitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bitmap)
        needToDrawShadow = false
    }


    private fun createShadowPlaceHolder(){
        bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
    }

}