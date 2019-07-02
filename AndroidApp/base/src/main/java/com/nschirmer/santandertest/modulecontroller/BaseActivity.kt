package com.nschirmer.santandertest.modulecontroller

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    private val moduleManager: SplitInstallManager by lazy {
        SplitInstallManagerFactory.create(this)
    }


    fun openModule(module: ModuleHelper){
        if(moduleManager.installedModules.contains(module.moduleName)){
            startModule(module)
        }
    }


    private fun startModule(module: ModuleHelper){
        Intent().setClassName(packageName, module.packageName).also { intent ->
            startActivity(intent)
        }
    }

}