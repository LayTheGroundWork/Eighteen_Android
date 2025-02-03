package com.eighteen.eighteenandroid.presentation

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.databinding.ActivityMainBinding
import com.eighteen.eighteenandroid.presentation.common.permission.PermissionManager
import com.eighteen.eighteenandroid.presentation.dialog.TwoButtonPopUpDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 *
 * @file MainActivity.kt
 * @date 05/08/2024
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val permissionManager = PermissionManager(this)

    private val myViewModel by viewModels<MyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationBar.itemIconTintList = null
        setupNavigation()
        initStateFlow()
        initFragmentResult()
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val navController = navHostFragment.navController

        // 바텀 네비게이션과 NavController 연결
        binding.bottomNavigationBar.setupWithNavController(navController)

        // 네비게이션 목적지 변경 시 바텀 네비게이션의 가시성 조정
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val visibleNavigationIds = listOf(
                R.id.fragmentMain,
                R.id.fragmentRanking,
                R.id.fragmentMyProfile,
                R.id.fragmentChat,
                R.id.teenMainFragment
            )
            binding.bottomNavigationBar.isVisible = visibleNavigationIds.contains(destination.id)
        }
        initRequiredLoginMenus()
    }

    private fun initRequiredLoginMenus() {
        val requiredLoginMenuIds = listOf(R.id.fragmentMyProfile)
        binding.bottomNavigationBar.menu.children.filter { requiredLoginMenuIds.contains(it.itemId) }
            .forEach {
                it.setOnMenuItemClickListener {
                    var isSuccess = false
                    requestWithRequiredLogin {
                        isSuccess = true
                    }
                    isSuccess.not()
                }
            }
    }

    private fun initStateFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                myViewModel.myProfileStateFlow.collect {
                    //TODO placeholder 추가
                    val myMenu =
                        binding.bottomNavigationBar.menu.children.find { menu -> menu.itemId == R.id.fragmentMyProfile }
                    if (it.isSuccess()) {
                        Glide.with(this@MainActivity).asBitmap()
                            .placeholder(R.drawable.ic_launcher_background).circleCrop()
                            .load(it.data?.medias?.firstOrNull()?.url)
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    val drawable = BitmapDrawable(resources, resource)
                                    myMenu?.icon = drawable
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {
                                }
                            })
                    } else {
                        myMenu?.icon = null
                    }
                }
            }
        }
    }

    private fun initFragmentResult() {
        supportFragmentManager.setFragmentResultListener(
            LOGIN_DIALOG_REQUEST_KEY,
            this,
            object : TwoButtonPopUpDialogFragment.TowButtonPopUpDialogFragmentResultListener() {
                override fun onConfirm() {
                    findNavController(R.id.fragment_container_view).navigate(R.id.fragmentLogin)
                }
            })
    }

    fun requestWithRequiredLogin(request: () -> Unit) {
        if (myViewModel.authTokenStateFlow.value == null) {
            showLoginDialog()
            return
        }
        request()
    }

    private fun showLoginDialog() {
        val dialogFragment =
            TwoButtonPopUpDialogFragment.newInstance(
                requestKey = LOGIN_DIALOG_REQUEST_KEY,
                title = getString(R.string.required_login_title),
                content = getString(R.string.required_login_content),
                rejectButtonText = getString(R.string.no),
                confirmButtonText = getString(R.string.yes)
            )
        dialogFragment.show(supportFragmentManager, null)
    }

    companion object {
        private const val LOGIN_DIALOG_REQUEST_KEY = "login_dialog_request_key"
    }
}