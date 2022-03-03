package com.example.acedropseller.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.acedropseller.repository.Datastore
import com.example.acedropseller.view.auth.AuthActivity
import com.example.acedropseller.view.dash.DashboardActivity
import com.example.acedropseller.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    private val profileViewModel: ProfileViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val datastore = Datastore(this)

        lifecycleScope.launch {
            if (datastore.isLogin()) {
                startActivity(
                    Intent(
                        this@SplashScreenActivity,
                        DashboardActivity::class.java
                    )
                )
                finish()
            } else {
                startActivity(Intent(this@SplashScreenActivity, AuthActivity::class.java))
                finish()
            }

        }
    }

}