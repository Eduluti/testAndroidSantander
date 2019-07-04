package com.nschirmer.login

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.core.view.isVisible
import com.nschirmer.santandertest.modulecontroller.BaseActivity
import com.nschirmer.santandertest.modulecontroller.ModuleHelper
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initLayout()
    }


    private fun initLayout(){
        login_logo.visibility = View.GONE
        login_background_transition.visibility = View.VISIBLE

        setInitialAnimation()

        login_button.setOnClickListener {
            checkInputs()
        }
    }


    private fun setInitialAnimation(){
        resources.displayMetrics.run {
            initScaleAnimation()
            initTranslationAnimation(this)
        }
    }



    private fun initScaleAnimation(){
        val pvhX = PropertyValuesHolder.ofFloat(View.SCALE_X,0f)
        val pvhY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f)
        ObjectAnimator.ofPropertyValuesHolder(login_background_transition, pvhX, pvhY).run {
            startDelay = 600
            duration = 150
            start()
        }
    }


    private fun initTranslationAnimation(displayMetrics: DisplayMetrics){
        val displayHalfHeight = displayMetrics.heightPixels.toFloat() / 2
        val offsetTopMargin = resources.getDimension(R.dimen.login_logo_top_margin)
        val logoHalfHeight = login_logo.height.toFloat() / 2
        val logoHalfDisplayHeight = displayHalfHeight - logoHalfHeight
        val backgroundTransitionOffset = offsetTopMargin -  displayHalfHeight + offsetTopMargin

        ValueAnimator.ofFloat(300f, 0f).run {
            startDelay = 200
            duration = 600
            addUpdateListener {
                // Due flickering, the view is Gone
                if(! login_logo.isVisible) login_logo.visibility = View.VISIBLE
                // from where the view is on the XML to where the logo is going to
                login_background_transition.y = interpolate(0f, backgroundTransitionOffset, it.animatedFraction)
                login_logo.y = interpolate(logoHalfDisplayHeight, offsetTopMargin, it.animatedFraction)
            }
            start()
        }
    }


    /**
     * Make interpolation calculation for transition animation.
     * @param from View start point in dimensions.
     * @param to View end point in dimensions.
     * @param ratio The ratio of interpolation like [ValueAnimator.getAnimatedFraction].
     *
     * @return Calculated offset dimension for the View.
     * **/
    private fun ValueAnimator.interpolate(from: Float, to: Float, ratio: Float) = from + ratio * (to - from)


    /**
     * Will check
     * **/
    private fun checkInputs(){

        login_input_email.errorText = "bla"

        //openAccount()
    }


    private fun openAccount(){
        openModule(ModuleHelper.STATEMENTS)
    }





}


