package com.eighteen.eighteenandroid.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.ActivityMainBinding
import androidx.fragment.app.commit
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.eighteen.eighteenandroid.presentation.home.MainFragment

/**
 *
 * @file MainActivity.kt
 * @date 05/08/2024
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationBar.itemIconTintList = null
        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val navController = navHostFragment.navController

        // 바텀 네비게이션과 NavController 연결
        binding.bottomNavigationBar.setupWithNavController(navController)

        // 네비게이션 목적지 변경 시 바텀 네비게이션의 가시성 조정
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigationBar.visibility = if (destination.id == R.id.fragmentMain) View.VISIBLE else View.GONE
        }
    }
}