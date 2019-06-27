package com.nschirmer.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import com.nschirmer.widgets.InputText.InputType.*
import kotlinx.android.synthetic.main.input_text.view.*


class InputText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0): FrameLayout(context, attrs, defStyle) {

    /** Set if the automatic error message will disappear if the user typed something when an error is appearing
     * The default value is true by default
     * **/
    var autoErrorRemoval = true

    /** Text of the edit text **/
    var text: String?
        set(text){
            inputtext_field.setText(text)

        } get() = inputtext_field.text.toString()


    /** Hint of the edit text **/
    var hint: String?
        set(hint) {
            inputtext_layout.hint = hint

        } get() = inputtext_layout.hint.toString()


    /** Set the max char lenght of input text
     *  @param length if the value is <= 0 then the maxLength is not affected
     * **/
    var maxLength: Int
        set(length) {
            if (length > 0) inputtext_field.maxEms = length

        } get() = inputtext_field.maxEms


    /** Content description of the text input for accessibility **/
    var description: String?
        set(description) {
            inputtext_layout.contentDescription = description ?: inputtext_layout.hint
        }
        get() = inputtext_layout.contentDescription.toString()


    /** Error text of the edit text
     * @param errorText if the value is null then the error label will be removed
     * **/
    var errorText: String?
        set(errorText) {
            inputtext_layout.error = errorText
            addTextChangedListener {
                if (autoErrorRemoval) removeError()
            }

        } get() = inputtext_layout.error.toString()


    /****/
    var color: Int? = Color.GRAY
        set(color) {
            if(color != null){
                field = color
                passwordTogleColor = color
                textColor = color
                hintColor = color
                cursorColor = color
                borderColor = color

            } else throw(NullPointerException("Null not allowed"))

        } get() {
            return when {
                allColorsMatch() -> field
                else -> null
            }
        }


    var cursorColor: Int? = Color.GRAY
        set(color) {
            if (color != null) {
                field = color
                inputtext_field.textCursorDrawable = ColorDrawable(color)

            } else throw(NullPointerException("Null not allowed"))
        }


    private fun allColorsMatch(): Boolean {
        return (color == borderColor) == (passwordTogleColor == hintColor)
    }

    var borderColor: Int? = Color.GRAY
        set(color) {
            if(color!= null){
                field = color
                setDefaultOutlineBox(color)
                inputtext_layout.boxStrokeColor = color

            } else throw(NullPointerException("Null not allowed"))

        }



    // Google not addressed the default outline box color yet.....
    private fun setDefaultOutlineBox(color: Int) {
        try {
            TextInputLayout::class.java.getDeclaredField("defaultStrokeColor").let {
                it.isAccessible = true
                it.set(inputtext_layout, color)
            }

        } catch (e: Exception) {
            Log.w("TAG", "Failed to change box color, item might look wrong")
        }
    }



    var passwordTogleColor: Int? = Color.GRAY
        set(color) {
            if(color != null){
                field = color
                ColorState(color).let {
                    inputtext_layout.setPasswordVisibilityToggleTintList(
                        ColorStateList(it.viewStates, it.getColorState())
                    )
                }
            } else throw(NullPointerException("Null not allowed"))
        }






    private class ColorState (val color: Int) {

        private companion object {
            private const val BRIGHTNESS_ADDITION = 0.8f
        }

        /**
         * States used on the input
         * @see ViewState
         * **/
        val viewStates: Array<IntArray> = arrayOf(
            ViewState.FOCUSED.state,
            ViewState.DEFAULT.state
        )


        /**
         * Colors by [viewStates]
         * */
        fun getColorState(): IntArray {
            val colorState = IntArray(viewStates.size)

            viewStates.forEachIndexed { index, state ->
                colorState[index] = when (state) {
                    ViewState.FOCUSED.state -> color
                    ViewState.DEFAULT.state -> getBrighterColor(color)
                    else -> throw(NoSuchFieldException("Color not defined for this state"))
                }
            }

            return colorState
        }


        /**
         * @return a [Color] brighter than [color]
         * **/
        private fun getBrighterColor(color: Int): Int {
            val hsv = FloatArray(3)
            Color.colorToHSV(color, hsv)
//            hsv[2] *= BRIGHTNESS_ADDITION // change the brightness of the color

            hsv[2] = 1.0f - BRIGHTNESS_ADDITION * (1.0f - hsv[2]);

            return Color.HSVToColor(hsv)
        }

    }



    var textColor: Int?
        set(color) {
            if(color != null){
                inputtext_field.setTextColor(color)

            } else throw(NullPointerException("Null not allowed"))

        } get () = inputtext_field.currentTextColor



    var hintColor: Int? = Color.GRAY
        set(color) {
            if(color != null){
                field = color
                ColorState(color).let {
                    inputtext_layout.defaultHintTextColor =
                        ColorStateList(it.viewStates, it.getColorState())
                }

            } else throw(NullPointerException("Null not allowed"))
        }



    var errorColor: Int? = Color.RED
        set(color) {
            if(color != null){
                field = color
                ColorState(color).let {
                    inputtext_layout.setErrorTextColor(
                        ColorStateList(it.viewStates, it.getColorState())
                    )
                }

            } else throw(NullPointerException("Null not allowed"))

        }


        /**
     * If there is need to add another type, add here in [InputType] AND on [attrs.xml] in order position order
     *
     * [TEXT] it's just a plain text type
     * [PASSWORD] By default not reveal the input and show the magic eye to show the password
     * [NUMBER] Only show numbers on the keyboard
     * [EMAIL] Set the keyboard layout as email and check if the email is a valid one
     * **/
    enum class InputType {
        TEXT,
        PASSWORD,
        NUMBER,
        EMAIL
    }


    init {
        LayoutInflater.from(context).inflate(R.layout.input_text, this, true)
        setAttributes(context.obtainStyledAttributes(attrs, R.styleable.InputText))
    }


    private fun setAttributes(typedArray: TypedArray){
        try{
            text = typedArray.getString(R.styleable.InputText_text) ?: text
            hint = typedArray.getString(R.styleable.InputText_hint) ?: hint
            maxLength = typedArray.getInteger(R.styleable.InputText_maxLength, 0)
            description = typedArray.getString(R.styleable.InputText_description) ?: getDefaultDescription()
            color = typedArray.getColor(R.styleable.InputText_color, Color.GRAY)
            errorColor = typedArray.getColor(R.styleable.InputText_error_color, Color.RED)
            // Set the input type based on the xml attribute. If there is no attribute then the TEXT type will be added
            setInputType(values()[typedArray.getInt(R.styleable.InputText_type, 0)])


        } finally {
            typedArray.recycle()
        }
    }


    private fun getDefaultDescription(): String {
        return when {
            description != null -> description!!
            text != null -> text!!
            hint != null -> hint!!
            else -> throw(java.lang.NullPointerException(
                "Not able to give a description. You need to fill the hint or the text")
                    )
        }
    }



    /** @return If the input is empty **/
    fun isEmpty(): Boolean {
        return if(inputtext_field.text != null) inputtext_field.text.toString().isEmpty() else true
    }


    /** Clear the input text **/
    fun clearText(){
        inputtext_field.text?.clear()
    }


    /** Remove focus from the input **/
    fun clearInputFocus(){
        inputtext_field.clearFocus()
        inputtext_layout.clearFocus()
    }


    /** Add focus to input **/
    fun requestInputFocus(){
        inputtext_field.requestFocus()
    }


    /** Set the mode that the input will operate **/
    fun setInputType(inputType: InputType){
        when (inputType){
            TEXT -> setInputText()
            PASSWORD -> setInputPassword()
            NUMBER -> setInputNumber()
            EMAIL -> setInputEmail()
        }
    }


    /** Set the input as plain text **/
    private fun setInputText(){
        inputtext_field.setRawInputType(android.text.InputType.TYPE_CLASS_TEXT)
    }


    // TODO color
    /** Set input as hidden content with the eye icon to show/hide **/
    private fun setInputPassword(){
        inputtext_field.setRawInputType(android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                or android.text.InputType.TYPE_CLASS_TEXT)
        inputtext_field.transformationMethod = PasswordTransformationMethod.getInstance()
    }


    /** Set the input as only numbers without decimal places **/
    private fun setInputNumber(){
        inputtext_layout.isPasswordVisibilityToggleEnabled = false
        inputtext_field.setRawInputType(android.text.InputType.TYPE_CLASS_NUMBER
                or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD)
        inputtext_field.transformationMethod = NumericKeyBoardTransformationMethod()
    }


    // Custom transformation to bypass some Android restrictions of keyboard ewith only numbers
    private inner class NumericKeyBoardTransformationMethod : PasswordTransformationMethod() {
        override fun getTransformation(source: CharSequence, view: View): CharSequence {
            return source
        }
    }


    // TODO validate email
    /** Set the input as e-mail **/
    private fun setInputEmail(){
        inputtext_field.setRawInputType(android.text.InputType.TYPE_CLASS_TEXT
                or  android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
    }


    fun cleanInput(){
        text = ""
    }


    /** Manually remove the error message **/
    fun removeError(){
        inputtext_layout.error = null
    }


    /** Check if is displaying an error message **/
    fun hasError(): Boolean {
        return inputtext_layout.error != null
    }


    /** Add an already created text listener **/
    fun addTextChangedListener(textWatcher: TextWatcher){
        inputtext_field.addTextChangedListener(textWatcher)
    }



    /** Remove an already created text listener
     * Useful to resolve some issues of the text listener
     * **/
    fun removeTextChangedListener(textWatcher: TextWatcher) {
        inputtext_field.removeTextChangedListener(textWatcher)
    }


    /** Edit text change listener lambda pass trough **/
    fun addTextChangedListener(listener: (Editable?) -> Unit ){
        inputtext_field.addTextChangedListener {
            listener(it)
        }
    }


    /** TODO comment **/
    fun setInputSelection(selectionIndex: Int){
        inputtext_field.setSelection(selectionIndex)
    }




}