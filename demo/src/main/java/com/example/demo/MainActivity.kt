package com.example.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.demo.databinding.ActivityMainBinding
import com.example.demo.extention.applyEdgeToEdgeInsets
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hubspot.mobilesdk.HubspotManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@SuppressLint("RestrictedApi")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment).navController
    }
    private val listener by lazy {
        NavController.OnDestinationChangedListener { _, destination, arguments ->
            val screensToHideLayout = listOf(R.id.navigation_webview, R.id.fragment_custom_chat_flow, R.id.fragment_sdk_option, R.id.fragment_setting)
            when {
                screensToHideLayout.contains(destination.id) -> {
                    binding.bottomNavView.visibility = View.GONE
                    supportActionBar?.setShowHideAnimationEnabled(false)
                    supportActionBar?.hide()
                }

                else -> {
                    binding.bottomNavView.visibility = View.VISIBLE
                    supportActionBar?.setShowHideAnimationEnabled(false)
                    supportActionBar?.show()
                }
            }
        }
    }

    @Inject
    lateinit var hubspotManager: HubspotManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        applyEdgeToEdgeInsets()
        val navView: BottomNavigationView = binding.bottomNavView
        val navController = findNavController(R.id.navHostFragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_fab_button, R.id.navigation_webview
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(listener)
    }

    override fun onPause() {
        navController.removeOnDestinationChangedListener(listener)
        super.onPause()
    }
}