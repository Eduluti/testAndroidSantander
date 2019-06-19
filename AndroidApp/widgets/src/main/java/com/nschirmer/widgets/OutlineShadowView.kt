package com.nschirmer.widgets

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout


/**
 *  This class create a shadow for a Child View, by creating a blurred bitmap within the Child View perimeter
 *
 *  Usage:
        <com.nschirmer.widgets.OutlineShadowView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            //TODO
            app:has_shadow="true"
            app:shadow_color="@color/blue">

            <SomeView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"/>

        </com.nschirmer.widgets.OutlineShadowView>
 *
 */

//This class has a lot of inspiration from ShadowLayout by Devlight
class OutlineShadowView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0): FrameLayout(context, attrs, defStyle) {

    // Default internal shadow values
    private val DEFAULT_RADIUS = 30.0f
    private val DEFAULT_DISTANCE = 15.0f
    private val DEFAULT_ANGLE = 45.0f
    private val DEFAULT_COLOR = Color.DKGRAY


    // Internal shadow bounds values
    private val MAX_ALPHA = 255
    private val MAX_ANGLE = 360.0f
    private val MIN_RADIUS = 0.1f
    private val MIN_ANGLE = 0.0f


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
    var alpha = 0
    var offsetX = 0f
    var offsetY = 0f
    var color = DEFAULT_COLOR
        set(color) {
            field = color
            alpha = Color.alpha(color)
            resetShadow()
        }

    var angle = DEFAULT_ANGLE
        set(value) {
            if(value in MIN_ANGLE .. MAX_ANGLE){
                field = Math.max(MIN_ANGLE, Math.min(value, MAX_ANGLE))
                resetShadow()
            }
        }

    var distance = DEFAULT_DISTANCE
        set(value) {
            field = value
            resetShadow()
        }

    var radius = DEFAULT_RADIUS
        set(value) {
            field = Math.max(MIN_RADIUS, value)

            if (!isInEditMode) {
                paint.maskFilter = BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
                resetShadow()
            }
        }

    var hasShadow: Boolean = true
        set(hasShadow) {
            field = hasShadow
            postInvalidate()
        }


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


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        clearShadowBitmap()
    }


    private fun clearShadowBitmap(){
        bitmap?.recycle()
        bitmap = null
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setShadowBounds()
    }


    private fun setShadowBounds(){
        bounds.set(0, 0, measuredWidth, measuredHeight)
    }


    override fun requestLayout() {
        needToDrawShadow = true
        super.requestLayout()
    }


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