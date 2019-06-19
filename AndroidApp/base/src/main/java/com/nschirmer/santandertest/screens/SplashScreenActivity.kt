package com.nschirmer.santandertest.screens

import android.os.Bundle
import com.nschirmer.santandertest.modulecontroller.BaseActivity
import com.nschirmer.santandertest.modulecontroller.ModuleHelper

class SplashScreenActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        openModule(ModuleHelper.LOGIN)
        finish()
    }

}