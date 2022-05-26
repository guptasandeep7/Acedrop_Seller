package com.acedrops.acedropseller.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.acedrops.acedropseller.repository.Datastore
import com.acedrops.acedropseller.view.auth.AuthActivity
import com.acedrops.acedropseller.view.dash.DashboardActivity
import com.acedrops.acedropseller.viewmodel.ProfileViewModel
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