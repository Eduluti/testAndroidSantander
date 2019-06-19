package com.nschirmer.login

import android.os.Bundle
import com.nschirmer.santandertest.modulecontroller.BaseActivity
import com.nschirmer.santandertest.modulecontroller.ModuleHelper
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button.setOnClickListener {
            openModule(ModuleHelper.STATEMENTS)
        }
    }
}
