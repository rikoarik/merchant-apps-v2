@file:Suppress("DEPRECATION")

package com.solusinegeri.merchant3.presentation.ui.main

import android.annotation.SuppressLint
import android.os.Vibrator
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.*
import androidx.navigation.fragment.NavHostFragment
import com.solusinegeri.merchant3.presentation.ui.compose.BottomNavigationCompose
import com.solusinegeri.merchant3.R
import com.solusinegeri.merchant3.core.base.BaseActivity
import com.solusinegeri.merchant3.core.utils.DynamicColors
import com.solusinegeri.merchant3.core.utils.BackPressedHandler
import com.solusinegeri.merchant3.data.repository.AuthRepository
import com.solusinegeri.merchant3.databinding.ActivityMainBinding
import com.solusinegeri.merchant3.presentation.ui.auth.LoginActivity
import com.solusinegeri.merchant3.presentation.viewmodel.AuthViewModel
import com.solusinegeri.merchant3.presentation.viewmodel.AuthViewModelFactory
import com.solusinegeri.merchant3.presentation.viewmodel.UiState
import com.solusinegeri.merchant3.core.security.SecurityThreat
import com.solusinegeri.merchant3.presentation.ui.compose.SecurityDialog

/**
 * MainActivity yang menggunakan BaseActivity untuk konsistensi
 */
class MainActivity : BaseActivity<ActivityMainBinding, AuthViewModel>() {

    private var backPressedTime: Long = 0
    private val backPressInterval: Long = 2000
    private var currentRoute: String = "home"
    private lateinit var gestureDetector: GestureDetector
    private lateinit var vibrator: Vibrator

    override val viewModel: AuthViewModel by lazy {
        val authRepository = AuthRepository(this)
        val factory = AuthViewModelFactory(authRepository)
        ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun setupUI() {
        super.setupUI()
        
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        setupNavigation()
        setupSwipeGesture()
        setupBackPressedHandler()
        performSecurityCheck()
    }
    
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(binding.navHostFragment.id) as NavHostFragment
        val navController = navHostFragment.navController
        
        setupComposeBottomNavigation(navController)
    }
    
    private fun setupComposeBottomNavigation(navController: androidx.navigation.NavController) {
        binding.composeBottomNav.setContent {
            var currentRouteState by remember { mutableStateOf(currentRoute) }
            
            LaunchedEffect(navController) {
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    val newRoute = when (destination.id) {
                        R.id.homeFragment -> "home"
                        R.id.profileFragment -> "profile"
                        R.id.newsFragment -> "news"
                        else -> "home"
                    }
                    currentRoute = newRoute
                    currentRouteState = newRoute
                }
            }
            
            BottomNavigationCompose(
                currentRoute = currentRouteState,
                onNavigate = { route ->
                    when (route) {
                        "home" -> {
                            if (currentRouteState != "home") {
                                navController.navigate(R.id.homeFragment)
                            }
                        }
                        "profile" -> {
                            if (currentRouteState != "profile") {
                                navController.navigate(R.id.profileFragment)
                            }
                        }
                        "news" -> {
                            if (currentRouteState != "news") {
                                navController.navigate(R.id.newsFragment)
                            }
                        }
                        "analytics" -> {
                            showSuccess("Analytics clicked")
//                            if (currentRouteState != "analytics") {
//                                navController.navigate(R.id.analyticsFragment)
//                            }
                        }
                    }
                },
                onFabClick = {
                    showSuccess("QR Scanner clicked")
                }
            )
        }
    }
    
    @SuppressLint("ClickableViewAccessibility")
    private fun setupSwipeGesture() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val diffX = e2.x - (e1?.x ?: 0f)
                val diffY = e2.y - (e1?.y ?: 0f)
                
                // Check if horizontal swipe is more significant than vertical
                if (kotlin.math.abs(diffX) > kotlin.math.abs(diffY)) {
                    if (kotlin.math.abs(diffX) > 50 && kotlin.math.abs(velocityX) > 50) {
                        // Haptic feedback
                        vibrator.vibrate(50)
                        
                        Log.d("SwipeNavigation", "Swipe detected: diffX=$diffX, velocityX=$velocityX, currentRoute=$currentRoute")
                        
                        if (diffX > 0) {
                            // Swipe right - go to previous menu
                            Log.d("SwipeNavigation", "Swipe right - navigating to previous menu")
                            navigateToPreviousMenu()
                        } else {
                            // Swipe left - go to next menu
                            Log.d("SwipeNavigation", "Swipe left - navigating to next menu")
                            navigateToNextMenu()
                        }
                        return true
                    }
                }
                return false
            }
        })
        
        // Set touch listener to the main container and nav host fragment
        binding.root.setOnTouchListener { _, event ->
            Log.d("SwipeNavigation", "Root touch event: ${event.action}")
            gestureDetector.onTouchEvent(event)
        }
        
        binding.navHostFragment.setOnTouchListener { _, event ->
            Log.d("SwipeNavigation", "NavHost touch event: ${event.action}")
            gestureDetector.onTouchEvent(event)
        }
    }
    
    private fun navigateToNextMenu() {
        when (currentRoute) {
            "home" -> {
                navigateToProfile()
            }
            "profile" -> {
                navigateToHome()
            }
            "news" -> {
                navigateToNews()
            }
            "analytics" -> {
//                navigateToNews()
            }
        }
    }
    
    private fun navigateToPreviousMenu() {
        when (currentRoute) {
            "home" -> {
                navigateToProfile()
            }
            "profile" -> {
                navigateToHome()
            }
            "news" -> {
                navigateToNews()
            }
            "analytics" -> {
//                navigateToNews()
            }
        }
    }
    
    private fun navigateToHome() {
        if (currentRoute != "home") {
            Log.d("SwipeNavigation", "Navigating to Home")
            val navHostFragment = supportFragmentManager
                .findFragmentById(binding.navHostFragment.id) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.homeFragment)
            currentRoute = "home"
        }
    }
    
    private fun navigateToProfile() {
        if (currentRoute != "profile") {
            Log.d("SwipeNavigation", "Navigating to Profile")
            val navHostFragment = supportFragmentManager
                .findFragmentById(binding.navHostFragment.id) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.profileFragment)
            currentRoute = "profile"
        }
    }

    private fun navigateToNews() {
        if (currentRoute != "news") {
            Log.d("SwipeNavigation", "Navigating to News")
            val navHostFragment = supportFragmentManager
                .findFragmentById(binding.navHostFragment.id) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.newsFragment)
            currentRoute = "news"
        }
    }



    private fun performSecurityCheck() {
        val securityChecker = com.solusinegeri.merchant3.core.security.SecurityChecker(this)
        securityChecker.performSecurityCheck(
            onThreatsDetected = { threats ->
                showSecurityDialog(threats)
            },
            onNoThreats = {
                // No threats detected, continue normally
            }
        )
    }
    
    private fun showSecurityDialog(threats: List<SecurityThreat>) {
        binding.composeBottomNav.setContent {
            SecurityDialog(
                threats = threats,
                onDismiss = {
                    // User chose to continue anyway
                },
                onExit = {
                    finishAffinity()
                }
            )
        }
    }


    private fun setupBackPressedHandler() {
        BackPressedHandler.addCallback(this) {
            val currentTime = System.currentTimeMillis()
            
            if (currentTime - backPressedTime <= backPressInterval) {
                finishAffinity()
            } else {
                backPressedTime = currentTime
                Toast.makeText(this, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}