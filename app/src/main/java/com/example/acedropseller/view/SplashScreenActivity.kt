package com.example.acedropseller.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.example.acedropseller.R
import com.example.acedropseller.repository.Datastore
import com.example.acedropseller.view.auth.AuthActivity
import com.example.acedropseller.view.dash.DashboardActivity
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val datastore = Datastore(this)

        lifecycleScope.launch {
            if (datastore.isLogin()) {
                startActivity(Intent(this@SplashScreenActivity, DashboardActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@SplashScreenActivity, AuthActivity::class.java))
                finish()
            }

        }
    }


}