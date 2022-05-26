package com.acedrops.acedropseller.view.dash

import android.app.AlertDialog.Builder
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.acedrops.acedropseller.R
import com.acedrops.acedropseller.databinding.ActivityDashboardBinding
import com.acedrops.acedropseller.viewmodel.ProfileViewModel

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container2) as NavHostFragment
        val navController: NavController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    override fun onBackPressed() =
        when (findNavController(R.id.container2).currentDestination?.id) {
            R.id.homeFragment -> alertBox()
            else -> super.onBackPressed()
        }

    private fun alertBox() {
        val builder = Builder(this)
        builder.setTitle("Exit")
            .setMessage("Are you sure you want to Exit?")
            .setPositiveButton("Exit") { dialog, id ->
                finish()
            }
            .setNeutralButton("Cancel") { dialog, id -> }
        val exit = builder.create()
        exit.show()
    }
}