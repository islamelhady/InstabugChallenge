package com.elhady.instabugchallenge


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.elhady.instabugchallenge.databinding.ActivityMainBinding
import com.elhady.instabugchallenge.utils.NetworkUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val navController: NavController by lazy {
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        navHostFragment.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("MainActivity", "onCreate: Activity created and binding initialized")

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.testingFragment, R.id.cacheFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavView.setupWithNavController(navController)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavView) { v, insets ->
            val visible = insets.isVisible(WindowInsetsCompat.Type.ime())
            binding.bottomNavView.visibility = if (visible) View.GONE else View.VISIBLE
            insets
        }
    }

    override fun onStart() {
        super.onStart()
        handleNetworkChanges()
        Log.d("MainActivity", "onStart: Network changes are being handled")
    }

    private fun handleNetworkChanges() {
        NetworkUtils.getNetworkLiveData(applicationContext).observe(this, Observer { isConnected ->
            Log.d("NetworkStatus", "Network connectivity status: $isConnected")
            updateNetworkStatus(isConnected)
        })
    }

    /**
     * Observe network changes
     */
    private fun updateNetworkStatus(isConnected: Boolean) {
        if (!isConnected) {
            binding.textViewNetworkStatus.text =
                getString(R.string.text_no_connectivity)
            binding.networkStatusLayout.apply {
                visibility = View.VISIBLE
                setBackgroundColor(getColor(R.color.red))
            }
        } else {
            binding.textViewNetworkStatus.text = getString(R.string.text_connectivity)
            binding.networkStatusLayout.apply {
                setBackgroundColor(getColor(R.color.green))
                animate()
                    .alpha(1f)
                    .setStartDelay(ANIMATION_DURATION)
                    .setDuration(ANIMATION_DURATION)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            visibility = View.GONE
                        }
                    })
            }
        }
    }

    companion object {
        const val ANIMATION_DURATION = 1000L
    }
}