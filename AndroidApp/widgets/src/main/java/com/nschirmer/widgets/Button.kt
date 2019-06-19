package com.nschirmer.widgets

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
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
            app:description="Hello sir" />
 * **/

class Button @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0): FrameLayout(context, attrs, defStyle) {

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



    init {
        LayoutInflater.from(context).inflate(R.layout.button, this, true)
        setAttributes(context.obtainStyledAttributes(attrs, R.styleable.Button))
    }


    private fun setAttributes(typedArray: TypedArray){
        try {
            title = typedArray.getString(R.styleable.Button_title) ?: title
            description = typedArray.getString(R.styleable.Button_description) ?: title

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


}